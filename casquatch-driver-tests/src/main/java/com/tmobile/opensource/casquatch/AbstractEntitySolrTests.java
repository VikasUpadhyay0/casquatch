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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class AbstractEntitySolrTests<T extends AbstractCasquatchEntity> extends AbstractEntityTests<T> {

    public AbstractEntitySolrTests(Class<T> tableClass) {
        super(tableClass);
    }

    @Test
    public void testGetAllBySolrObject() {
        T obj = prepObject();

        List<T> tstObj = this.getCasquatchDao().getAllBySolr(this.getTableClass(),obj);
        assertEquals(1, tstObj.size());
        assertEquals(obj, tstObj.get(0));

        cleanObject(obj);
    }


    @Test
    public void testGetCountBySolrObject() {
        T obj = prepObject();

        Long count = this.getCasquatchDao().getCountBySolr(this.getTableClass(),obj);
        assertEquals(1, (long) count);

        cleanObject(obj);
    }

    @Test
    public void testGetAllBySolrObjectWithOptions() {
        T obj = prepObject();

        List<T> tstObj = this.getCasquatchDao().getAllBySolr(this.getTableClass(),obj,queryOptions);
        assertEquals(1, tstObj.size());
        assertEquals(obj, tstObj.get(0));

        cleanObject(obj);
    }


    @Test
    public void testGetCountBySolrObjectWithOptions() {
        T obj = prepObject();

        Long count = this.getCasquatchDao().getCountBySolr(this.getTableClass(),obj,queryOptions);
        assertEquals(1, (long) count);

        cleanObject(obj);
    }

}
