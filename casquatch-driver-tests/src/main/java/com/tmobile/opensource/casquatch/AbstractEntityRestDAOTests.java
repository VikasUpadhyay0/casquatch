/*
 * Copyright 2018 T-Mobile US, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tmobile.opensource.casquatch;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.opensource.casquatch.rest.Request;
import com.tmobile.opensource.casquatch.rest.Response;
import com.tmobile.opensource.casquatch.tests.podam.CasquatchPodamFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.jemos.podam.api.PodamFactory;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public abstract class AbstractEntityRestDAOTests<T extends AbstractCasquatchEntity, S>{

    private static final PodamFactory podamFactory = new CasquatchPodamFactoryImpl();

    protected abstract S getService();
    protected abstract MockMvc getMockMvc();
    protected abstract CasquatchDao getDao();

    private final Class<T> tableClass;
    private final Class<S> serviceClass;

    /**
     * Construct with reference classes
     * @param tableClass table class reference
     * @param serviceClass service class reference
     */
    public AbstractEntityRestDAOTests(Class<T> tableClass, Class<S> serviceClass) {
        this.tableClass=tableClass;
        this.serviceClass=serviceClass;
    }

    /**
     * Deserialize a string response into a response object
     * @param response string response
     * @return response object
     */
    private Response<T> deserialize(String response) {
        ObjectMapper mapper = new ObjectMapper();

        JavaType type = mapper.getTypeFactory().constructParametricType(Response.class, this.tableClass);
        try {
            return mapper.readValue(response, type);
        } catch (IOException e) {
            log.error("Unable to deserialize response",e);
            return new Response<>(Response.Status.ERROR);
        }
    }

    /**
     * Helper function to get api name via reflection
     * @param method method name
     * @return api name
     */
    private String getApi(String method) {
        try {
            return
                this.serviceClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()[0]+
                this.serviceClass.getMethod(method,Request.class).getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()[0];
        } catch (NoSuchMethodException e) {
            log.error("Unable to load API",e);
            return null;
        }
    }

    /**
     * Perform a given API
     * @param method method name
     * @param request request object
     * @param expectedResponse expected response object
     * @throws Exception any exception thrown by procedure
     */
    private void doApi(String method, Request<T> request, Response<T> expectedResponse) throws Exception{
        log.trace("Request: {}",request.toString());
        log.trace("Expected Response: {}",expectedResponse.toString());
        String api = getApi(method);
        assertNotNull(api);
        log.trace("API: {}",api);

        MvcResult result = this.getMockMvc().perform(MockMvcRequestBuilders.post(api)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()))
                .andExpect(status().isOk())
                .andReturn();

        Response<T> actualResponse = deserialize(result.getResponse().getContentAsString());

        log.trace("Raw Response: {}",result.getResponse().getContentAsString());
        log.trace("Expected Response: {}",expectedResponse.toString());
        log.trace("Actual Response: {}",actualResponse.toString());

        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        if(expectedResponse.getPayload()==null) {
            assertNull(actualResponse.getPayload());
        }
        else {
            assertEquals(expectedResponse.getPayload().size(), actualResponse.getPayload().size());
            for(int i=0;i<expectedResponse.getPayload().size();i++) {
                assertEquals(expectedResponse.getPayload().get(i), actualResponse.getPayload().get(i));
            }
        }
    }

    @Test
    public void testGet() throws Exception {

        T payload = podamFactory.manufacturePojoWithFullData(this.tableClass);
        this.getDao().save(this.tableClass,payload);

        doApi("get",new Request<>(payload),new Response<>(payload));

        this.getDao().delete(this.tableClass,payload);

    }

    @Test
    public void testSave() throws Exception {

        T payload = podamFactory.manufacturePojoWithFullData(this.tableClass);

        doApi("save",new Request<>(payload),new Response<>(Response.Status.SUCCESS));

        this.getDao().delete(this.tableClass,payload);

    }

}

