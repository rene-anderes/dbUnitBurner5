package org.anderes.edu.dbunitburner;

import java.io.IOException;
import java.net.URL;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.util.fileloader.AbstractDataFileLoader;

public class JsonDataFileLoader extends AbstractDataFileLoader {

    @Override
    public IDataSet loadDataSet(URL url) throws DataSetException, IOException {
        return new JsonDataSet(url);
    }

}
