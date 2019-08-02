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

package com.tmobile.opensource.casquatch.policies;


import com.datastax.oss.driver.api.core.cql.Statement;

public class DefaultFailoverPolicy extends FailoverPolicy {
    @Override
    public Boolean shouldFailover(Exception exception, Statement statement) {
        if(exception instanceof com.datastax.oss.driver.api.core.AllNodesFailedException) {
            return true;
        }
        else if(exception instanceof com.datastax.oss.driver.api.core.DriverTimeoutException) {
            return true;
        }
        else if(exception instanceof com.datastax.oss.driver.api.core.InvalidKeyspaceException) {
            return true;
        }
        else //noinspection RedundantIfStatement
            if(exception instanceof com.datastax.oss.driver.api.core.NoNodeAvailableException) {
            return true;
        }
        else {
            return false;
        }
    }
}
