package uk.ac.kcl.stranders.hitour.utilities;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.kcl.stranders.hitour.database.DBWrap;
import uk.ac.kcl.stranders.hitour.database.NotInSchemaException;
import uk.ac.kcl.stranders.hitour.model.Data;
import uk.ac.kcl.stranders.hitour.model.Point;
import uk.ac.kcl.stranders.hitour.model.Tour;
import uk.ac.kcl.stranders.hitour.model.TourSession;

import static uk.ac.kcl.stranders.hitour.database.schema.DatabaseConstants.*;

public class DataManipulation {

    /**
     * Add a tour session to all relevant tables in database and make list of data to be downloaded
     * @param tourSession TourSession object that gives information on particular session
     * @param tour Tour object that is used by tourSession
     * @param context the Context of addition to the database
     * @param database the DBWrap which wraps database to which additions should be made
     * @return
     */
    public static ArrayList<String> addSession(TourSession tourSession, Tour tour, Context context, DBWrap database) {

        ArrayList<String> urlArrayList = new ArrayList<>();

        // Add the tour session to the local database
        Map<String,String> tourSessionColumnsMap = new HashMap<>();
        tourSessionColumnsMap.put(TOUR_ID, tourSession.getTourId().toString());
        tourSessionColumnsMap.put(START_DATE, tourSession.getStartDate());
        tourSessionColumnsMap.put(DURATION, tourSession.getDuration().toString());
        tourSessionColumnsMap.put(PASSPHRASE, tourSession.getPassphrase());
        tourSessionColumnsMap.put(NAME, tourSession.getName());
        Map<String,String> tourSessionPrimaryKeysMap = new HashMap<>();
        tourSessionPrimaryKeysMap.put(SESSION_ID, tourSession.getId().toString());
        try {
            database.insert(tourSessionColumnsMap, tourSessionPrimaryKeysMap, SESSION_TABLE);
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        // Add the tour to the local database
        Map<String,String> tourColumnsMap = new HashMap<>();
        tourColumnsMap.put(NAME, tour.getName());
        tourColumnsMap.put(AUDIENCE_ID, tour.getAudienceId().toString());
        tourColumnsMap.put(QUIZ_URL, tour.getQuizUrl());
        Map<String, String> tourPrimaryKeysMap = new HashMap<>();
        tourPrimaryKeysMap.put(TOUR_ID, tour.getId().toString());
        try {
            database.insert(tourColumnsMap, tourPrimaryKeysMap, TOUR_TABLE);
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        // Add points to the local database
        List<Point> points = tour.getPoints();
        for(Point point : points) {
            Map<String,String> pointColumnMap = new HashMap<>();
            pointColumnMap.put(NAME, point.getName());
            pointColumnMap.put(URL, point.getUrl());
            pointColumnMap.put(DESCRIPTION, point.getDescription());
            Map<String,String> pointPrimaryKeysMap = new HashMap<>();
            pointPrimaryKeysMap.put(POINT_ID, point.getId().toString());
            try {
                database.insert(pointColumnMap, pointPrimaryKeysMap, POINT_TABLE);
            } catch(NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
            // Add url of the header image to list to be downloaded to local storage
            urlArrayList.add(point.getUrl());
            // Add data to the local database
            List<Data> data = point.getData();
            for (int i = 0; i < data.size(); i ++) {
                Data datum = data.get(i);
                Map<String, String> datumColumnsMap = new HashMap<>();
                datumColumnsMap.put(URL, datum.getUrl());
                datumColumnsMap.put(DESCRIPTION, datum.getDescription());
                datumColumnsMap.put(TITLE, datum.getTitle());
                Map<String, String> datumPrimaryKeysMap = new HashMap<>();
                datumPrimaryKeysMap.put(DATA_ID, datum.getId().toString());
                try {
                    database.insert(datumColumnsMap, datumPrimaryKeysMap, DATA_TABLE);
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
                // Add url of the physical data to list to be downloaded to local storage
                urlArrayList.add(datum.getUrl());
                // Add point data to the local database
                Map<String, String> pointDatumColumnsMap = new HashMap<>();
                pointDatumColumnsMap.put(RANK, datum.getRank().toString());
                Map<String, String> pointDataPrimaryKeysMap = new HashMap<>();
                pointDataPrimaryKeysMap.put(POINT_ID, point.getId().toString());
                pointDataPrimaryKeysMap.put(DATA_ID, datum.getId().toString());
                try {
                    database.insert(pointDatumColumnsMap, pointDataPrimaryKeysMap, POINT_DATA_TABLE);
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
                // Add data audience to the local database
                Map<String, String> dataAudienceColumnsMap = new HashMap<>();
                Map<String, String> dataAudiencePrimaryKeysMap = new HashMap<>();
                dataAudiencePrimaryKeysMap.put(DATA_ID, datum.getId().toString());
                dataAudiencePrimaryKeysMap.put(AUDIENCE_ID, tour.getAudienceId().toString());
                try {
                    database.insert(dataAudienceColumnsMap, dataAudiencePrimaryKeysMap, AUDIENCE_DATA_TABLE);
                } catch (NotInSchemaException e) {
                    Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
                }
            }
            // Add tour points to the local database
            Map<String, String> tourPointColumnsMap = new HashMap<>();
            tourPointColumnsMap.put(RANK, point.getRank().toString());
            tourPointColumnsMap.put(UNLOCK, UNLOCK_STATE_LOCKED);
            Map<String, String> tourPointPrimaryKeysMap = new HashMap<>();
            tourPointPrimaryKeysMap.put(TOUR_ID, tour.getId().toString());
            tourPointPrimaryKeysMap.put(POINT_ID, point.getId().toString());
            try {
                database.insert(tourPointColumnsMap, tourPointPrimaryKeysMap, POINT_TOUR_TABLE);
            } catch (NotInSchemaException e) {
                Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
            }
        }

        // Add audience to the local database
        Map<String, String> audienceColumnsMap = new HashMap<>();
        Map<String, String> audiencePrimaryKeysMap = new HashMap<>();
        audiencePrimaryKeysMap.put(AUDIENCE_ID, tour.getAudienceId().toString());
        try {
            database.insert(audienceColumnsMap, audiencePrimaryKeysMap, AUDIENCE_TABLE);
        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }

        // Remove any URLs of data that should not be downloaded again
        ArrayList<String> toRemove = new ArrayList<>();
        for(String url : urlArrayList) {
            String filename = Utilities.createFilename(url);
            String localPath = context.getFilesDir().toString();
            File tempFile = new File(localPath + "/" + filename);
            if (tempFile.exists()) {
                toRemove.add(url);
            }
        }

        urlArrayList.removeAll(toRemove);

        return  urlArrayList;
    }

    /**
     * Remove a session and all now irrelevant data from the database
     * Also delete any irrelevant data from internal storage
     * @param sessionId the ID of the session to be removed
     * @param context the context of the removal
     */
    public static void removeSession(String sessionId, Context context, DBWrap database) {
        try {
            // Remove session from session table making note of the TOUR_ID
            Map<String,String> columnsMapSession = new HashMap<>();
            Map<String,String> primaryKeysMapSession = new HashMap<>();
            primaryKeysMapSession.put(SESSION_ID, sessionId);
            Cursor exactSessionCursor = database.getWholeByPrimary(SESSION_TABLE, primaryKeysMapSession);
            exactSessionCursor.moveToFirst();
            String completedTourId = exactSessionCursor.getString(exactSessionCursor.getColumnIndex(TOUR_ID));
            database.delete(columnsMapSession, primaryKeysMapSession, SESSION_TABLE);

            // Get the updated session table
            Cursor sessionCursor = database.getAll(SESSION_TABLE);

            // If no other sessions now exist remove all entries from all tables
            if (sessionCursor.getCount() == 0) {
                database.deleteAll(TOUR_TABLE);
                database.deleteAll(POINT_TOUR_TABLE);
                Cursor pointCursor = database.getAll(POINT_TABLE);
                for(int i = 0; i < pointCursor.getCount(); i++) {
                    pointCursor.moveToPosition(i);
                    String url = pointCursor.getString(pointCursor.getColumnIndex(URL));
                    deleteDataFile(url, context);
                }
                database.deleteAll(POINT_TABLE);
                database.deleteAll(POINT_DATA_TABLE);
                Cursor dataCursor = database.getAll(DATA_TABLE);
                for(int i = 0; i < dataCursor.getCount(); i++) {
                    dataCursor.moveToPosition(i);
                    String url = dataCursor.getString(dataCursor.getColumnIndex(URL));
                    deleteDataFile(url, context);
                }
                database.deleteAll(DATA_TABLE);
                database.deleteAll(AUDIENCE_DATA_TABLE);
                database.deleteAll(AUDIENCE_TABLE);
                // No other checks needed, so exit the method
                return;
            }

            // Check if other sessions use the same tour
            for (int i = 0; i < sessionCursor.getCount(); i++) {
                sessionCursor.moveToPosition(i);
                if (sessionCursor.getString(sessionCursor.getColumnIndex(TOUR_ID)).equals(completedTourId)) {
                    // Nothing else should be deleted as tours use same data, so exit the method
                    return;
                }
            }

            // Remove tour from tour table
            Map<String,String> columnsMapTour = new HashMap<>();
            Map<String,String> primaryKeysMapTour = new HashMap<>();
            primaryKeysMapTour.put(TOUR_ID, completedTourId);
            Cursor exactTourCursor = database.getWholeByPrimary(TOUR_TABLE, primaryKeysMapTour);
            exactTourCursor.moveToFirst();
            String completedAudienceId = exactTourCursor.getString(exactTourCursor.getColumnIndex(AUDIENCE_ID));
            database.delete(columnsMapTour, primaryKeysMapTour, TOUR_TABLE);

            // Check to see if other tours use same audience, if not remove the audience from the AUDIENCE table
            Cursor updatedTourCursor = database.getAll(TOUR_TABLE);
            boolean audienceStillNeeded = false;
            for(int i = 0; i < updatedTourCursor.getCount(); i++) {
                updatedTourCursor.moveToPosition(i);
                String audienceId = updatedTourCursor.getString(updatedTourCursor.getColumnIndex(AUDIENCE_ID));
                if(audienceId.equals(completedAudienceId)) {
                    audienceStillNeeded = true;
                    break;
                }
            }
            if(!audienceStillNeeded) {
                Map<String, String> columnsMapAudience = new HashMap<>();
                Map<String, String> primaryKeysMapAudience = new HashMap<>();
                primaryKeysMapAudience.put(AUDIENCE_ID, completedAudienceId);
                database.delete(columnsMapAudience, primaryKeysMapAudience, AUDIENCE_TABLE);
                database.delete(columnsMapAudience, primaryKeysMapAudience, AUDIENCE_DATA_TABLE);
            }

            // Get list of POINT_IDs that tour used and delete rows for tour from POINT_TOUR table
            ArrayList<String> forRemovingPointIdArrayList = new ArrayList<>();
            Map<String, String> columnsMapPointTour = new HashMap<>();
            Map<String, String> primaryKeysMapPointTour = new HashMap<>();
            primaryKeysMapPointTour.put(TOUR_ID, completedTourId);
            Cursor completedPointTourCursor = database.getWholeByPrimaryPartial(POINT_TOUR_TABLE, primaryKeysMapPointTour);
            for(int i = 0; i < completedPointTourCursor.getCount(); i++) {
                completedPointTourCursor.moveToPosition(i);
                String tempPointId = completedPointTourCursor.getString(completedPointTourCursor.getColumnIndex(POINT_ID));
                forRemovingPointIdArrayList.add(tempPointId);
            }
            database.delete(columnsMapPointTour, primaryKeysMapPointTour, POINT_TOUR_TABLE);

            // Go through POINT_TOUR table to find which, if any, other tours use same points
            ArrayList<String> stillNeededPointIdArrayList = new ArrayList<>();
            for(String pointId : forRemovingPointIdArrayList) {
                // Get Cursor that shows other tours that use this POINT_ID
                Map<String, String> primaryKeysMapUpdatedPointTour = new HashMap<>();
                primaryKeysMapUpdatedPointTour.put(POINT_ID, pointId);
                Cursor updatedPointTourCursor = database.getWholeByPrimaryPartial(POINT_TOUR_TABLE, primaryKeysMapUpdatedPointTour);
                if(updatedPointTourCursor.getCount() > 0) {
                    // Add to list of points that cannot be deleted as used by other tours
                    stillNeededPointIdArrayList.add(pointId);
                }
            }
            // Remove POINT_IDs from list if they are still needed
            forRemovingPointIdArrayList.removeAll(stillNeededPointIdArrayList);

            // Remove all points that are no longer needed from POINT and POINT_DATA tables
            ArrayList<String> dataIdArrayList = new ArrayList<>();
            // Also make note of DATA_IDs used by these no longer needed points
            for(String pointId : forRemovingPointIdArrayList) {
                Map<String, String> columnsMapRemoving = new HashMap<>();
                Map<String, String> primaryKeysMapRemoving = new HashMap<>();
                primaryKeysMapRemoving.put(POINT_ID, pointId);
                database.delete(columnsMapRemoving, primaryKeysMapRemoving, POINT_TABLE);
                Cursor pointDataRemovingCursor = database.getWholeByPrimaryPartial(POINT_DATA_TABLE, primaryKeysMapRemoving);
                for(int i = 0; i < pointDataRemovingCursor.getCount(); i++) {
                    pointDataRemovingCursor.moveToPosition(i);
                    String tempDataId = pointDataRemovingCursor.getString(pointDataRemovingCursor.getColumnIndex(DATA_ID));
                    if(!dataIdArrayList.contains(tempDataId))
                        dataIdArrayList.add(tempDataId);
                }
                database.delete(columnsMapRemoving, primaryKeysMapRemoving, POINT_DATA_TABLE);
            }

            // Make note of DATA_IDs of data from points that are still needed
            for(String pointId : stillNeededPointIdArrayList) {
                Map<String, String> primaryKeysMapKeeping = new HashMap<>();
                primaryKeysMapKeeping.put(POINT_ID, pointId);
                Cursor pointDataKeepingCursor = database.getWholeByPrimaryPartial(POINT_DATA_TABLE, primaryKeysMapKeeping);
                for(int i = 0; i < pointDataKeepingCursor.getCount(); i++) {
                    pointDataKeepingCursor.moveToPosition(i);
                    String tempDataId = pointDataKeepingCursor.getString(pointDataKeepingCursor.getColumnIndex(DATA_ID));
                    if(!dataIdArrayList.contains(tempDataId))
                        dataIdArrayList.add(tempDataId);
                }
            }

            // Make note all points that use a piece of data that was used by the removed tour
            HashMap<String, ArrayList<String>> dataPointsMap = new HashMap<>();
            for(String dataId : dataIdArrayList) {
                Map<String, String> primaryKeysMapPointData = new HashMap<>();
                primaryKeysMapPointData.put(DATA_ID, dataId);
                Cursor tempPointDataCursor = database.getWholeByPrimaryPartial(POINT_DATA_TABLE, primaryKeysMapPointData);
                ArrayList<String> tempPointIdArrayList = new ArrayList<>();
                for(int i = 0; i < tempPointDataCursor.getCount(); i++) {
                    tempPointDataCursor.moveToPosition(i);
                    String tempPointId = tempPointDataCursor.getString(tempPointDataCursor.getColumnIndex(POINT_ID));
                    tempPointIdArrayList.add(tempPointId);
                }
                dataPointsMap.put(dataId, tempPointIdArrayList);
            }

            // Make note of all tours that use each point
            Cursor pointCursor = database.getAll(POINT_TABLE);
            Map<String, ArrayList<String>> pointToursMap = new HashMap<>();
            for(int i = 0; i < pointCursor.getCount(); i++) {
                pointCursor.moveToPosition(i);
                String pointId = pointCursor.getString(pointCursor.getColumnIndex(POINT_ID));
                ArrayList<String> tourIds = new ArrayList<>();

                Map<String, String> primaryKeysMapSpecificPoint = new HashMap<>();
                primaryKeysMapSpecificPoint.put(POINT_ID, pointId);
                Cursor pointTourCursor = database.getWholeByPrimaryPartial(POINT_TOUR_TABLE, primaryKeysMapSpecificPoint);
                for(int j = 0; j < pointTourCursor.getCount(); j++) {
                    pointTourCursor.moveToPosition(j);
                    tourIds.add(pointTourCursor.getString(pointTourCursor.getColumnIndex(TOUR_ID)));
                }
                pointToursMap.put(pointId, tourIds);
            }

            // Make connection between TOUR_ID and the AUDIENCE_ID of that tour
            Cursor tourCursor = database.getAll(TOUR_TABLE);
            Map<String, String> tourAudienceMap = new HashMap<>();
            for(int i = 0; i < tourCursor.getCount(); i++) {
                tourCursor.moveToPosition(i);
                String tourId = tourCursor.getString(tourCursor.getColumnIndex(TOUR_ID));
                String audienceId = tourCursor.getString(tourCursor.getColumnIndex(AUDIENCE_ID));
                tourAudienceMap.put(tourId, audienceId);
            }

            // Make list of all audiences that a piece of data is available to
            Map<String, ArrayList<String>> dataAudiencesMap = new HashMap<>();
            for(int i = 0; i < dataIdArrayList.size(); i++) {
                String dataId = dataIdArrayList.get(i);
                Map<String, String> primaryKeysMapDataAudience = new HashMap<>();
                primaryKeysMapDataAudience.put(DATA_ID, dataId);
                ArrayList<String> audienceIdArrayList = new ArrayList<>();
                Cursor dataAudienceCursor = database.getWholeByPrimaryPartial(AUDIENCE_DATA_TABLE, primaryKeysMapDataAudience);
                for(int j = 0; j < dataAudienceCursor.getCount(); j++) {
                    dataAudienceCursor.moveToPosition(j);
                    String audienceId = dataAudienceCursor.getString(dataAudienceCursor.getColumnIndex(AUDIENCE_ID));
                    audienceIdArrayList.add(audienceId);
                }
                dataAudiencesMap.put(dataId, audienceIdArrayList);
            }

            // Go through and remove any relevant entries from POINT_DATA, DATA, and DATA_AUDIENCE tables
            for(Map.Entry<String, ArrayList<String>> entry : dataPointsMap.entrySet()) {
                // If data not used by any points then remove it from POINT_DATA, DATA, and DATA_AUDIENCE tables
                ArrayList<String> points = entry.getValue();
                boolean dataUsed = false;
                for (int i = 0; i < points.size(); i++) {
                    String pointId = points.get(i);
                    ArrayList<String> tours = pointToursMap.get(pointId);
                    // The data can only be used by this point if a tour that uses it exists
                    if (tours.size() > 0) {
                        ArrayList<String> tourAudiences = new ArrayList<>();
                        for (int j = 0; j < tours.size(); j++) {
                            String tourId = tours.get(j);
                            tourAudiences.add(tourAudienceMap.get(tourId));
                        }
                        ArrayList<String> dataAudiences = dataAudiencesMap.get(entry.getKey());
                        dataAudiences.retainAll(tourAudiences);
                        // If they share at least one audience then the data is used
                        if (dataAudiences.size() > 0) {
                            dataUsed = true;
                            break;
                        }
                    }
                }
                // If no tour exists that uses the data then remove it
                if (!dataUsed) {
                    removeData(entry.getKey(), context, database);
                }
            }

        } catch (NotInSchemaException e) {
            Log.e("DATABASE_FAIL", Log.getStackTraceString(e));
        }
    }

    /**
     * Remove a piece of data from all relevant tables in the database and the device's storage
     * without infringing on other data that might need access to the file
     * @param dataId id of the datum to be removed
     * @param context Context of the removal
     * @param database the DBWrap from which it should be removed
     * @throws NotInSchemaException
     */
    private static void removeData(String dataId, Context context, DBWrap database) throws NotInSchemaException {
        HashMap<String, String> columnsMap = new HashMap<>();
        HashMap<String, String> primaryKeysMap = new HashMap<>();
        primaryKeysMap.put(DATA_ID, dataId);
        Cursor dataCursor = database.getWholeByPrimary(DATA_TABLE, primaryKeysMap);
        dataCursor.moveToFirst();
        String url = dataCursor.getString(dataCursor.getColumnIndex(URL));
        if(!usedByPoint(url, database)) {
            deleteDataFile(url, context);
        }
        database.delete(columnsMap, primaryKeysMap, DATA_TABLE);
        database.delete(columnsMap, primaryKeysMap, AUDIENCE_DATA_TABLE);
        database.delete(columnsMap, primaryKeysMap, POINT_DATA_TABLE);
    }

    /**
     * Delete the file from the device if it exists
     * @param url url of the data
     * @param context Context in which deletion should take place
     */
    private static void deleteDataFile(String url, Context context) {
        String filename = Utilities.createFilename(url);
        filename = context.getFilesDir().toString() + "/" + filename;
        File file = new File(filename);
        if(file.exists()) {
            file.delete();
        }
    }

    /**
     * Check if a piece of datum is used by a point for its image
     * @param dataUrl the url the datum was downloaded from
     * @param database the DBWrap from which they both come
     * @return true if is still needed, false if it is not
     * @throws NotInSchemaException
     */
    private static boolean usedByPoint(String dataUrl, DBWrap database) throws NotInSchemaException {
        Cursor pointCursor = database.getAll(POINT_TABLE);
        for(int i = 0; i < pointCursor.getCount(); i++) {
            pointCursor.moveToPosition(i);
            String pointUrl = pointCursor.getString(pointCursor.getColumnIndex(URL));
            if(dataUrl.equals(pointUrl))
                return true;
        }
        return false;
    }

}
