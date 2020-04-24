package org.apache.iotdb.db.index;

import java.util.List;
import java.util.Map;
import org.apache.iotdb.db.index.common.DataFileInfo;
import org.apache.iotdb.db.index.common.IndexManagerException;
import org.apache.iotdb.tsfile.read.common.BatchData;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.utils.Pair;

/**
 * Each StorageGroupProcessor contains a IndexProcessor, and each IndexProcessor can contain more than one IIndex.
 * Each type of Index corresponds one IIndex.
 *
 */
public interface IIndex {

    /**
     *
     */
    void init();

    /**
     *
     * Given the file list contain path, create index files.
     * Call this method when the index create operation happens or the merge file has created.
     *
     */
    byte[] createAndFlushIndex(BatchData batchData)throws IndexManagerException;
//    boolean build(Path path, List<DataFileInfo> fileList, Map<String, Object> parameters);

    /**
     *
     * Given one new file contain path, create the index file
     * Call this method when the close operation has completed.
     *
     * @param path       the time series to be indexed
     * @param newFile    the new file contain path
     * @param parameters other parameters
     * @return
     * @throws IndexManagerException
     */
    boolean build(Path path, DataFileInfo newFile, Map<String, Object> parameters)
            throws IndexManagerException;

    /**
     * Given the new file list after merge, delete all index files which are not in the list,
     * and switch to the new index files along with the new data files.
     * Call this method after the merge operation has completed. Block index read and write during this process.
     *
     * @param newFileList the data files leaves after the merge operation, the column paths in the file list need to
     *                    build index, some one may has no data in some data file
     * @return whether the operation is successful
     * @throws IndexManagerException if the given column path is not correct or some base service occurred error
     */
    boolean mergeSwitch(Path path, List<DataFileInfo> newFileList) throws IndexManagerException;

    /**
     * todo
     *
     * @param path
     * @param timestamp
     * @param value
     */
    void append(Path path, long timestamp, String value);

    /**
     * todo
     *
     * @param path
     * @param timestamp
     * @param value
     */
    void update(Path path, long timestamp, String value);

    /**
     * todo
     *
     * @param path
     * @param starttime
     * @param endtime
     * @param value
     */
    void update(Path path, long starttime, long endtime, String value);

    /**
     * todo
     *
     * @param path
     * @param timestamp
     */
    void delete(Path path, long timestamp);

    /**
     * todo。
     *
     * @return todo
     * @throws IndexManagerException
     */
    boolean close() throws IndexManagerException;

    /**
     * drop the index created on path.
     *
     * @param path the column path
     * @return whether the operation is successful
     * @throws IndexManagerException
     */
    boolean drop(Path path) throws IndexManagerException;

    /**
     * query on path with parameters, return result by limitSize
     *
     * @param path               the path to be queried
     * @param parameters         the query request with all parameters
     * @param nonUpdateIntervals the query request with all parameters
     * @param limitSize          the limitation of number of answers
     * @return the query response
     */
    Object query(Path path, List<Object> parameters, List<Pair<Long, Long>> nonUpdateIntervals,
        int limitSize)
            throws IndexManagerException;
}