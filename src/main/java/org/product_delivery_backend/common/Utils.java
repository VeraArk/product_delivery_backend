package org.product_delivery_backend.common;

import java.io.File;

public interface Utils {
static
long fileSize(String filename)
{
        File file = new File(filename);

        if (!file.exists() || !file.isFile()) return -1;

        return file.length();
}

static
String setExtension(String path, String ext)
{
        int idx = path.lastIndexOf('.');

        if(idx == -1)
                return path;

        return path.substring(0, idx) + '.' + ext;
}
}
