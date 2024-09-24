package org.product_delivery_backend.config;

import java.util.Properties;

public interface UseProps {
Properties props = Props.get();
String fileStorage = "file-storage";
}