/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kogito.serverless.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.json.Json;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.net.URI;
import java.util.UUID;

public class CEProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {
        Order message = exchange.getIn().getBody(Order.class);

        //ObjectMapper mapper = new ObjectMapper();
        //String orderMessageObj = mapper.writeValueAsString(message);

        CloudEventImpl<Order> orderCE =
                CloudEventBuilder.<Order>builder()
                        .withId(UUID.randomUUID().toString())
                        .withType("newOrderEvent")
                        .withSource(URI.create("http://localhost:8080"))
                        .withData(message)
                        .build();
        exchange.getIn().setBody(Json.encode(orderCE));
    }
}
