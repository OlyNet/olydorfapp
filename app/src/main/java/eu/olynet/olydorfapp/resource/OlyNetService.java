/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.olynet.olydorfapp.resource;

import java.util.List;

import eu.olynet.olydorfapp.model.CategoryItem;
import eu.olynet.olydorfapp.model.CategoryMetaItem;
import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.DrinkItem;
import eu.olynet.olydorfapp.model.DrinkMetaItem;
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

import static android.R.attr.value;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
interface OlyNetService {

    @GET("{type}/{id}/{field}")
    Call<ResponseBody> getImage(@Header("User-Agent") String userAgent,
                                @Path("type") String type,
                                @Path("id") int id,
                                @Path("field") String field);

    /* News API */
    @GET("news/meta/{id}")
    Call<NewsMetaItem> getMetaNews(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("news/meta/")
    Call<List<NewsMetaItem>> getMetaNews(@Header("User-Agent") String userAgent);

    @GET("news/{id}")
    Call<NewsItem> getNews(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("news/any/{ids}")
    Call<List<NewsItem>> getNews(@Header("User-Agent") String userAgent,
                                 @Path(value = "ids", encoded = true) MultiID ids);

    @GET("news")
    Call<List<NewsItem>> getNews(@Header("User-Agent") String userAgent);

    /* Food API */
    @GET("food/meta/{id}")
    Call<FoodMetaItem> getMetaFood(@Header("User-Agent") String userAgent,
                                   @Path("id") int id);

    @GET("food/meta/")
    Call<List<FoodMetaItem>> getMetaFood(@Header("User-Agent") String userAgent);

    @GET("food/{id}")
    Call<FoodItem> getFood(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("food/any/{ids}")
    Call<List<FoodItem>> getFood(@Header("User-Agent") String userAgent,
                                 @Path(value = "ids", encoded = true) MultiID ids);

    @GET("food")
    Call<List<FoodItem>> getFood(@Header("User-Agent") String userAgent);

    /* MealOfTheDay API */
    @GET("mealoftheday/meta/{id}")
    Call<MealOfTheDayMetaItem> getMetaMealoftheday(@Header("User-Agent") String userAgent,
                                                   @Path("id") int id);

    @GET("mealoftheday/meta/")
    Call<List<MealOfTheDayMetaItem>> getMetaMealoftheday(@Header("User-Agent") String userAgent);

    @GET("mealoftheday/{id}")
    Call<MealOfTheDayItem> getMealoftheday(@Header("User-Agent") String userAgent,
                                           @Path("id") int id);

    @GET("mealoftheday/any/{ids}")
    Call<List<MealOfTheDayItem>> getMealoftheday(@Header("User-Agent") String userAgent,
                                                 @Path(value = "ids", encoded = true) MultiID ids);

    @GET("mealoftheday")
    Call<List<MealOfTheDayItem>> getMealoftheday(@Header("User-Agent") String userAgent);

    /* Category API */
    @GET("category/meta/{id}")
    Call<CategoryMetaItem> getMetaCategory(@Header("User-Agent") String userAgent,
                                           @Path("id") int id);

    @GET("category/meta/")
    Call<List<CategoryMetaItem>> getMetaCategory(@Header("User-Agent") String userAgent);

    @GET("category/{id}")
    Call<CategoryItem> getCategory(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("category/any/{ids}")
    Call<List<CategoryItem>> getCategory(@Header("User-Agent") String userAgent,
                                         @Path(value = "ids", encoded = true) MultiID ids);

    @GET("category")
    Call<List<CategoryItem>> getCategory(@Header("User-Agent") String userAgent);

    /* Drink API */
    @GET("drink/meta/{id}")
    Call<DrinkMetaItem> getMetaDrink(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("drink/meta/")
    Call<List<DrinkMetaItem>> getMetaDrink(@Header("User-Agent") String userAgent);

    @GET("drink/{id}")
    Call<DrinkItem> getDrink(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("drink/any/{ids}")
    Call<List<DrinkItem>> getDrink(@Header("User-Agent") String userAgent,
                                   @Path(value = "ids", encoded = true) MultiID ids);

    @GET("drink")
    Call<List<DrinkItem>> getDrink(@Header("User-Agent") String userAgent);

    /* DailyMeal API */
    @GET("dailymeal/meta/{id}")
    Call<DailyMealMetaItem> getMetaDailymeal(@Header("User-Agent") String userAgent,
                                             @Path("id") int id);

    @GET("dailymeal/meta/")
    Call<List<DailyMealMetaItem>> getMetaDailymeal(@Header("User-Agent") String userAgent);

    @GET("dailymeal/{id}")
    Call<DailyMealItem> getDailymeal(@Header("User-Agent") String userAgent, @Path("id") int id);

    @GET("dailymeal/any/{ids}")
    Call<List<DailyMealItem>> getDailymeal(@Header("User-Agent") String userAgent,
                                           @Path(value = "ids", encoded = true) MultiID ids);

    @GET("dailymeal")
    Call<List<DailyMealItem>> getDailymeal(@Header("User-Agent") String userAgent);

    /* Organization API */
    @GET("organization/meta/{id}")
    Call<OrganizationMetaItem> getMetaOrganization(@Header("User-Agent") String userAgent,
                                                   @Path("id") int id);

    @GET("organization/meta/")
    Call<List<OrganizationMetaItem>> getMetaOrganization(@Header("User-Agent") String userAgent);

    @GET("organization/{id}")
    Call<OrganizationItem> getOrganization(@Header("User-Agent") String userAgent,
                                           @Path("id") int id);

    @GET("organization/any/{ids}")
    Call<List<OrganizationItem>> getOrganization(@Header("User-Agent") String userAgent,
                                                 @Path(value = "ids", encoded = true) MultiID ids);

    @GET("organization")
    Call<List<OrganizationItem>> getOrganization(@Header("User-Agent") String userAgent);

}
