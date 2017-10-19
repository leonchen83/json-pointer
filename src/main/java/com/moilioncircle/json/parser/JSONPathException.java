/*
 * Copyright 2015-2018 Leon Chen
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moilioncircle.json.parser;

/**
 * @author Leon Chen
 */
public class JSONPathException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JSONPathException() {
        super();
    }

    public JSONPathException(String message) {
        super(message);
    }

    public JSONPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONPathException(Throwable cause) {
        super(cause);
    }

    protected JSONPathException(String message, Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
