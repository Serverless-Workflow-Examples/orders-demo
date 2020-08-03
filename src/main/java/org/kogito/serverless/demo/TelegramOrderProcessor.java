package org.kogito.serverless.demo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.telegram.model.IncomingMessage;

public class TelegramOrderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        final IncomingMessage message = exchange.getIn().getBody(IncomingMessage.class);
        exchange.getIn().setHeader("buyer", message.getFrom().getUsername());
        exchange.getIn().setBody(String.format("%s,%s", message.getFrom().getUsername(), message.getText()));
    }
}
