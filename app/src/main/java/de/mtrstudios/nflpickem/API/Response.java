/*
 * Copyright 2014 MTRamin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.mtrstudios.nflpickem.API;

/**
 * Base response class. All responses from the server will have this basic layout.
 * Status indicates if an operation returned a valid result
 * Data contains the actual response data
 */
public class Response<T> {
    protected String status;
    protected T data;

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
