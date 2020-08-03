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

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "telegram.token", defaultValue = "12345")
    String token;

    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    String broker;

    @Override
    public void configure() throws Exception {
        final DataFormat bindy = new BindyCsvDataFormat(Order.class);
        fromF("telegram:bots?authorizationToken=1355018939:%s", token)
                .process(new TelegramOrderProcessor())
                .unmarshal(bindy)
                .process(new CEProcessor())
                .log("event to send: ${body}")
                .toF("kafka:neworder?brokers=%s", broker);
    }
}
