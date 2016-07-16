package eu.olynet.olydorfapp.tests;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;

import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.OrganizationItem;

/**
 * Tests the parcelling of all concrete items that are derived from AbstractMetaItem.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@RunWith(AndroidJUnit4.class)
public class ModelParcellingTests {

    private static final byte[] nullImage = null;
    private static final byte[] emptyImage = {};
    private static final byte[] smallImage = new byte[50 * 1024];
    private static final byte[] largeImage = new byte[1024 * 1024];

    private static final OrganizationItem dummyOrganizationItem
            = new OrganizationItem(786, new Date(), new Date(), "create", "edit", new Date(),
                                   "http://example.com", null, new Date(),
                                   "Dummy " + "Organization",
                                   "dummy", "A dummy OrganizationItem for testing purposes",
                                   largeImage);

    private static final DailyMealItem dummyDailyMealItem = new DailyMealItem(3546, new Date(),
                                                                              new Date(), "create",
                                                                              "edit", new Date(),
                                                                              null,
                                                                              dummyOrganizationItem,
                                                                              new Date(),
                                                                              "Geiles Essen",
                                                                              "Fancy food", false,
                                                                              3.4f, largeImage);

    private static final String dummyText = "Lorem ipsum dolor sit amet, consetetur sadipscing " +
                                            "elitr, sed diam nonumy eirmod tempor invidunt ut " +
                                            "labore et dolore magna aliquyam " +
                                            "erat, sed diam voluptua. At vero eos et accusam et " +
                                            "justo duo dolores et ea rebum. " +
                                            "Stet clita kasd gubergren, no sea takimata sanctus " +
                                            "est Lorem ipsum dolor sit amet. " +
                                            "Lorem ipsum dolor sit amet, consetetur sadipscing " +
                                            "elitr, sed diam nonumy eirmod " +
                                            "tempor invidunt ut labore et dolore magna aliquyam " +
                                            "erat, sed diam voluptua. At vero " +
                                            "eos et accusam et justo duo dolores et ea rebum. " +
                                            "Stet clita kasd gubergren, no sea " +
                                            "takimata sanctus est Lorem ipsum dolor sit amet. " +
                                            "Lorem ipsum dolor sit amet, " +
                                            "consetetur sadipscing elitr, sed diam nonumy eirmod " +
                                            "tempor invidunt ut labore et " +
                                            "dolore magna aliquyam erat, sed diam voluptua. At " +
                                            "vero eos et accusam et justo duo " +
                                            "dolores et ea rebum. Stet clita kasd gubergren, no " +
                                            "sea takimata sanctus est Lorem " +
                                            "ipsum dolor sit amet.   \n";

    static {
        Arrays.fill(smallImage, Byte.MAX_VALUE);
        Arrays.fill(largeImage, Byte.MIN_VALUE);
    }

    @Test
    public void testOrganizationItem() {
        OrganizationItem original1 = new OrganizationItem(786, new Date(), new Date(), "create",
                                                          "edit", new Date(), "http://example.com",
                                                          null, new Date(), "Dummy Organization",
                                                          "dummy", dummyText, nullImage);
        Parcel parcel1 = Parcel.obtain();
        original1.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        OrganizationItem reconstructed1 = OrganizationItem.CREATOR.createFromParcel(parcel1);
        Assert.assertTrue(reconstructed1.exactlyEquals(original1));

        OrganizationItem original2 = new OrganizationItem(4567, new Date(), new Date(), "create",
                                                          "edit", new Date(), "http://example.com",
                                                          null, new Date(), "Dummy Organization",
                                                          "dummy", dummyText, emptyImage);
        Parcel parcel2 = Parcel.obtain();
        original2.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        OrganizationItem reconstructed2 = OrganizationItem.CREATOR.createFromParcel(parcel2);
        Assert.assertTrue(reconstructed2.exactlyEquals(original2));

        OrganizationItem original3 = new OrganizationItem(5768, new Date(), new Date(), "create",
                                                          "edit", new Date(), "http://example.com",
                                                          null, new Date(), "Dummy Organization",
                                                          "dummy", dummyText, smallImage);
        Parcel parcel3 = Parcel.obtain();
        original3.writeToParcel(parcel3, 0);
        parcel3.setDataPosition(0);
        OrganizationItem reconstructed3 = OrganizationItem.CREATOR.createFromParcel(parcel3);
        Assert.assertTrue(reconstructed3.exactlyEquals(original3));

        OrganizationItem original4 = new OrganizationItem(324, new Date(), new Date(), "create",
                                                          "edit", new Date(), "http://example.com",
                                                          null, new Date(), "Dummy Organization",
                                                          "dummy", dummyText, smallImage);
        Parcel parcel4 = Parcel.obtain();
        original4.writeToParcel(parcel4, 0);
        parcel4.setDataPosition(0);
        OrganizationItem reconstructed4 = OrganizationItem.CREATOR.createFromParcel(parcel4);
        Assert.assertTrue(reconstructed4.exactlyEquals(original4));
    }

    @Test
    public void testDailyMealItem() {
        DailyMealItem original1 = new DailyMealItem(48765, new Date(), new Date(), "create", "edit",
                                                    new Date(), null, dummyOrganizationItem,
                                                    new Date(), "Geiles Essen", "Fancy food",
                                                    true, 3.4f, nullImage);
        Parcel parcel1 = Parcel.obtain();
        original1.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        DailyMealItem reconstructed1 = DailyMealItem.CREATOR.createFromParcel(parcel1);
        Assert.assertTrue(reconstructed1.exactlyEquals(original1));

        DailyMealItem original2 = new DailyMealItem(39734, new Date(), new Date(), "create", "edit",
                                                    new Date(), null, dummyOrganizationItem,
                                                    new Date(), "Geiles Essen", "Fancy food",
                                                    true, 3.4f, emptyImage);
        Parcel parcel2 = Parcel.obtain();
        original2.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        DailyMealItem reconstructed2 = DailyMealItem.CREATOR.createFromParcel(parcel2);
        Assert.assertTrue(reconstructed2.exactlyEquals(original2));

        DailyMealItem original3 = new DailyMealItem(67899, new Date(), new Date(), "create", "edit",
                                                    new Date(), null, dummyOrganizationItem,
                                                    new Date(), "Geiles Essen", "Fancy food",
                                                    true, 3.4f, smallImage);
        Parcel parcel3 = Parcel.obtain();
        original3.writeToParcel(parcel3, 0);
        parcel3.setDataPosition(0);
        DailyMealItem reconstructed3 = DailyMealItem.CREATOR.createFromParcel(parcel3);
        Assert.assertTrue(reconstructed3.exactlyEquals(original3));

        DailyMealItem original4 = new DailyMealItem(3546, new Date(), new Date(), "create", "edit",
                                                    new Date(), null, dummyOrganizationItem,
                                                    new Date(), "Geiles Essen", "Fancy food",
                                                    true, 3.4f, largeImage);
        Parcel parcel4 = Parcel.obtain();
        original4.writeToParcel(parcel4, 0);
        parcel4.setDataPosition(0);
        DailyMealItem reconstructed4 = DailyMealItem.CREATOR.createFromParcel(parcel4);
        Assert.assertTrue(reconstructed4.exactlyEquals(original4));
    }

    @Test
    public void testFoodItem() {
        FoodItem original1 = new FoodItem(45678, new Date(), new Date(), "create", "edit",
                                          new Date(), null, dummyOrganizationItem, new Date(),
                                          "Geiles Essen", "Fancy food", true, 3.4f, nullImage);
        Parcel parcel1 = Parcel.obtain();
        original1.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        FoodItem reconstructed1 = FoodItem.CREATOR.createFromParcel(parcel1);
        Assert.assertTrue(reconstructed1.exactlyEquals(original1));

        FoodItem original2 = new FoodItem(456, new Date(), new Date(), "create", "edit", new Date(),
                                          null, dummyOrganizationItem, new Date(), "Geiles Essen",
                                          "Fancy food", false, 3.4f, emptyImage);
        Parcel parcel2 = Parcel.obtain();
        original2.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        FoodItem reconstructed2 = FoodItem.CREATOR.createFromParcel(parcel2);
        Assert.assertTrue(reconstructed2.exactlyEquals(original2));

        FoodItem original3 = new FoodItem(7857, new Date(), new Date(), "create", "edit",
                                          new Date(), null, dummyOrganizationItem, new Date(),
                                          "Geiles Essen", "Fancy food", true, 3.4f, smallImage);
        Parcel parcel3 = Parcel.obtain();
        original3.writeToParcel(parcel3, 0);
        parcel3.setDataPosition(0);
        FoodItem reconstructed3 = FoodItem.CREATOR.createFromParcel(parcel3);
        Assert.assertTrue(reconstructed3.exactlyEquals(original3));

        FoodItem original4 = new FoodItem(22134, new Date(), new Date(), "create", "edit",
                                          new Date(), null, dummyOrganizationItem, new Date(),
                                          "Geiles Essen", "Fancy food", false, 3.4f, largeImage);
        Parcel parcel4 = Parcel.obtain();
        original4.writeToParcel(parcel4, 0);
        parcel4.setDataPosition(0);
        FoodItem reconstructed4 = FoodItem.CREATOR.createFromParcel(parcel4);
        Assert.assertTrue(reconstructed4.exactlyEquals(original4));
    }

    @Test
    public void testMealOfTheDayItem() {
        MealOfTheDayItem original1 = new MealOfTheDayItem(456783, new Date(), new Date(), "create",
                                                          "edit", new Date(), null, new Date(),
                                                          "cook", 3.4f, dummyDailyMealItem,
                                                          nullImage);
        Parcel parcel1 = Parcel.obtain();
        original1.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        MealOfTheDayItem reconstructed1 = MealOfTheDayItem.CREATOR.createFromParcel(parcel1);
        Assert.assertTrue(reconstructed1.exactlyEquals(original1));

        MealOfTheDayItem original2 = new MealOfTheDayItem(2345, new Date(), new Date(), "create",
                                                          "edit", new Date(), null, new Date(),
                                                          "cook", 3.4f, dummyDailyMealItem,
                                                          emptyImage);
        Parcel parcel2 = Parcel.obtain();
        original2.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        MealOfTheDayItem reconstructed2 = MealOfTheDayItem.CREATOR.createFromParcel(parcel2);
        Assert.assertTrue(reconstructed2.exactlyEquals(original2));

        MealOfTheDayItem original3 = new MealOfTheDayItem(12351, new Date(), new Date(), "create",
                                                          "edit", new Date(), null, new Date(),
                                                          "cook", 3.4f, dummyDailyMealItem,
                                                          smallImage);
        Parcel parcel3 = Parcel.obtain();
        original3.writeToParcel(parcel3, 0);
        parcel3.setDataPosition(0);
        MealOfTheDayItem reconstructed3 = MealOfTheDayItem.CREATOR.createFromParcel(parcel3);
        Assert.assertTrue(reconstructed3.exactlyEquals(original3));

        MealOfTheDayItem original4 = new MealOfTheDayItem(84, new Date(), new Date(), "create",
                                                          "edit", new Date(), null, new Date(),
                                                          "cook", 3.4f, dummyDailyMealItem,
                                                          largeImage);
        Parcel parcel4 = Parcel.obtain();
        original4.writeToParcel(parcel4, 0);
        parcel4.setDataPosition(0);
        MealOfTheDayItem reconstructed4 = MealOfTheDayItem.CREATOR.createFromParcel(parcel4);
        Assert.assertTrue(reconstructed4.exactlyEquals(original4));
    }

    @Test
    public void testNewsItem() {
        NewsItem original1 = new NewsItem(134251324, new Date(), new Date(), "create", "edit",
                                          new Date(), "http://example.com", new Date(),
                                          dummyOrganizationItem, "Title", dummyText, nullImage);
        Parcel parcel1 = Parcel.obtain();
        original1.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        NewsItem reconstructed1 = NewsItem.CREATOR.createFromParcel(parcel1);
        Assert.assertTrue(reconstructed1.exactlyEquals(original1));

        NewsItem original2 = new NewsItem(456873, new Date(), new Date(), "create", "edit",
                                          new Date(), "http://example.com", new Date(),
                                          dummyOrganizationItem, "Title", dummyText, emptyImage);
        Parcel parcel2 = Parcel.obtain();
        original2.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        NewsItem reconstructed2 = NewsItem.CREATOR.createFromParcel(parcel2);
        Assert.assertTrue(reconstructed2.exactlyEquals(original2));

        NewsItem original3 = new NewsItem(789064, new Date(), new Date(), "create", "edit",
                                          new Date(), "http://example.com", new Date(),
                                          dummyOrganizationItem, "Title", dummyText, smallImage);
        Parcel parcel3 = Parcel.obtain();
        original3.writeToParcel(parcel3, 0);
        parcel3.setDataPosition(0);
        NewsItem reconstructed3 = NewsItem.CREATOR.createFromParcel(parcel3);
        Assert.assertTrue(reconstructed3.exactlyEquals(original3));

        NewsItem original4 = new NewsItem(2345, new Date(), new Date(), "create", "edit",
                                          new Date(), "http://example.com", new Date(),
                                          dummyOrganizationItem, "Title", dummyText, largeImage);
        Parcel parcel4 = Parcel.obtain();
        original4.writeToParcel(parcel4, 0);
        parcel4.setDataPosition(0);
        NewsItem reconstructed4 = NewsItem.CREATOR.createFromParcel(parcel4);
        Assert.assertTrue(reconstructed4.exactlyEquals(original4));
    }
}
