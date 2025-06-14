package com.example.canieat;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response", strict = false)
public class FoodBarcodeResponse {

    @Element(name = "body", required = false)
    public Body body;

    @Root(name = "body", strict = false)
    public static class Body {

        @ElementList(name = "items", inline = true, required = false)
        public List<Item> items;
    }

    @Root(name = "item", strict = false)
    public static class Item {

        @Element(name = "PRDLST_NM", required = false)
        public String prdlstNm;

        @Element(name = "BAR_CD", required = false)
        public String barCd;
    }
}