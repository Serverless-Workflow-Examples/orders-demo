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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",", isOrdered = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @DataField(pos = 1)
    private String byer;
    @DataField(pos = 2)
    private String shipto;
    @DataField(pos = 3)
    private String shipment;
    @DataField(pos = 4)
    private String product;
    @DataField(pos = 5)
    private String cost;

    public String getByer() {
        return byer;
    }

    public void setByer(String byer) {
        this.byer = byer;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public String getShipto() {
        return shipto;
    }

    public void setShipto(String shipto) {
        this.shipto = shipto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(byer, order.byer) &&
                Objects.equals(shipto, order.shipto) &&
                Objects.equals(shipment, order.shipment) &&
                Objects.equals(product, order.product) &&
                Objects.equals(cost, order.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(byer, shipto, shipment, product, cost);
    }
}
