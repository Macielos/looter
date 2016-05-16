package pl.looter;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;

import pl.looter.appengine.domain.pointApi.model.GeoPt;
import pl.looter.appengine.domain.pointApi.model.JsonMap;
import pl.looter.appengine.domain.pointApi.model.Point;
import pl.looter.appengine.domain.pointApi.model.PointTreeCollection;
import pl.looter.utils.GameplayUtils;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PointTreeCollectionTest {

    private static final String TAG = PointTreeCollectionTest.class.getSimpleName();

    @Test
    public void testPointTreeCollection() throws Exception {
        PointTreeCollection collection = new PointTreeCollection().setTreePoints(new JsonMap()).setTrees(new JsonMap());

        Point p1010 = newPoint(10f, 10f);
        Point p1015 = newPoint(10f, 15f);
        Point p1020 = newPoint(10f, 20f);
        Point p1520 = newPoint(15f, 20f);
	    Assert.assertTrue(GameplayUtils.connectNode(collection, null, p1010, null, "1010"));
	    Assert.assertTrue(GameplayUtils.connectNode(collection, p1010, p1015, "1010", "1015"));
	    Assert.assertTrue(GameplayUtils.connectNode(collection, p1015, p1020, "1015", "1020"));
	    Assert.assertTrue(GameplayUtils.connectNode(collection, p1015, p1520, "1015", "1520"));

	    /**
	     * 1010 -> 1015 -> 1020
	     *              -> 1520
	     *
	     */

	    Assert.assertEquals(4, collection.getTreePoints().size());
	    Assert.assertTrue(collection.getTreePoints().containsKey("1010"));
	    Assert.assertTrue(collection.getTreePoints().containsKey("1015"));
	    Assert.assertTrue(collection.getTreePoints().containsKey("1020"));
	    Assert.assertTrue(collection.getTreePoints().containsKey("1520"));

	    Assert.assertEquals(1, collection.getTrees().size());
	    JsonMap jsonMap = (JsonMap) collection.getTrees().get("1010");
	    Assert.assertEquals(4, jsonMap.size());
	    Assert.assertTrue(jsonMap.containsKey("1010"));
	    Assert.assertTrue(jsonMap.containsKey("1015"));
	    Assert.assertTrue(jsonMap.containsKey("1020"));
	    Assert.assertTrue(jsonMap.containsKey("1520"));
	    Assert.assertEquals(Arrays.asList("1015"), jsonMap.get("1010"));
	    Assert.assertEquals(Arrays.asList("1020", "1520"), jsonMap.get("1015"));
	    Assert.assertNull(jsonMap.get("1020"));
	    Assert.assertNull(jsonMap.get("1520"));

	    Assert.assertFalse(GameplayUtils.removeNode(collection, "1010"));
	    Assert.assertFalse(GameplayUtils.removeNode(collection, "1015"));
	    Assert.assertTrue(GameplayUtils.removeNode(collection, "1020"));
	    Assert.assertTrue(GameplayUtils.removeNode(collection, "1520"));
	    Assert.assertTrue(GameplayUtils.removeNode(collection, "1015"));

	    Assert.assertEquals(1, collection.getTrees().size());
	    jsonMap = (JsonMap) collection.getTrees().get("1010");
	    Assert.assertEquals(1, jsonMap.size());
	    Assert.assertTrue(jsonMap.containsKey("1010"));
	    Assert.assertFalse(jsonMap.containsKey("1015"));
	    Assert.assertFalse(jsonMap.containsKey("1020"));
	    Assert.assertFalse(jsonMap.containsKey("1520"));

	    System.out.println(collection.toPrettyString());
    }

    private Point newPoint(float latitude, float longitude) {
        return new Point().setGeoPoint(new GeoPt().setLatitude(latitude).setLongitude(longitude));
    }
}