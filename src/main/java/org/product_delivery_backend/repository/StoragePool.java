package org.product_delivery_backend.repository;

import java.io.InputStream;

public interface StoragePool {
String path(String filename);
long store(InputStream in, String filename);
InputStream load(String filename);
}
