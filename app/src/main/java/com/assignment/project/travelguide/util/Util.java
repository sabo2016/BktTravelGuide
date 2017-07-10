package com.assignment.project.travelguide.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.assignment.project.travelguide.database.model.Place;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by ukesh on 2/13/17.
 */

public class Util {


    public static final String MAP_FILE = "nepal.map";

    public static boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    public static void checkIfLocationIsEnabled(Context context) {
        LocationManager service = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Toast.makeText(context, "Please turn your GPS on for better application performance.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ((Activity) context).startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static ArrayList<Place> getStaticPlaces() {
        ArrayList<Place> stat_places = new ArrayList<Place>();

        stat_places.add(new Place(1, "Dattatreya Temple", 27.67349, 85.43539));
        stat_places.add(new Place(2,"Peacock Window",27.67317,85.43565));
        stat_places.add(new Place(3,"Bhairava nath Temple",27.67108,85.42951));
        stat_places.add(new Place(4, "Nyatapola Temple", 27.67139, 85.42936));
        stat_places.add(new Place(5, "Bhaktapur Durbar Square", 27.67213, 85.42825));
        stat_places.add(new Place(6,"55 windows palace",27.67218, 85.42859));
        stat_places.add(new Place(7,"Big Bell",27.6720433, 85.4284589));
        stat_places.add(new Place(8,"Golden Gate",27.67215, 85.42843));
        stat_places.add(new Place(9,"Mini Pashupatinath Temple",27.67184, 85.42848));
        stat_places.add(new Place(10,"National Art Gallery Musuem",27.67226,85.42847));

        return stat_places;
    }

    public static int destinationId;

    public static void setDestination(int placeId) {
        Util.destinationId = placeId;
    }

    public static Place getDestination() {
        ArrayList<Place> places = getStaticPlaces();
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getPlaceId() == destinationId) {
                return places.get(i);
            }
        }
        return null;
    }

    public static HashMap<String, String> getDestinationDetail(int placeId) {
        HashMap<String, String> temp = new HashMap<String, String>();

        switch (placeId) {


            case 10:
                temp.put("pic_path","artgallery.jpg");
                temp.put("detail","National Art Gallery musuem is best known for its rich collection of scroll paintings and breath taking artworks in stone. Visitors can get the information about the history of Bhaktapur.");
                break;

            case 9:
                temp.put("pic_path","minipasupatinath.jpg");
                temp.put("detail","The holy god Shiva temple, the mini pashupati, is believed to be built right in front of the palace after a Bhadgoan king dreamed of it. Local people have strong faith towards god Shiva.");
                break;

            case 8:
                temp.put("pic_path","goldengate.jpg");
                temp.put("detail","Golden Gate is unparallel specimen of repousse art dating back to 1756. It is the entrance to the marvelous taleju temple complex. Getting into it leads to a number of artistically designed chowks (courtyards) including Royal Bath,which is adorned with well-admired golden faucet among others.");
                break;

            case 7:
                temp.put("pic_path","bigbell.jpg");
                temp.put("detail","Big-Bell was erected by Ranajit Malla Bhaktapur’s last Malla king. It was used in those days for paying homage to Goddess Taleju,the lineage deity of Malla rulers,as well as to call assemblies of the citizens to discuss on given subjects concerning the state. Nowadays, it is rung twice a day as a mark of tribute to goddess. ");
                break;

            case 6:
                temp.put("pic_path","55windowpalace.jpg");
                temp.put("detail","(Pachpanna Jhyale Durbar) was built during the reign of the Malla King Yaksha Malla in 1427 AD and was remodeled by King Bhupatindra Malla in the 17th century. Among the brick walls, with their gracious setting and sculptural design, is a balcony of fifty-five windows, considered to be a unique masterpiece of woodcarving.It is also known as one of the oldest monument present in nepal.");
                break;

            case 5:
                temp.put("pic_path", "bhaktapur-durbar-square.jpg");
                temp.put("detail", "It is a gem for the entire nation and here world-renowed 55 window palace is the most fascinating structure. Visitors all around the world admires the elaborately carved windows and doors. National Art Gallery museum exists here which is best known for its rich collection of paubha scroll paintings and breath-taking arworks in stone.\n" +
                        "\n" +
                        "     World famous Golden Gate also lies here which is an unparalleled specimen of art dating back to 1756, it is the entrance to the marvelous Taleju Teple Complex.Getting into it leads to a number of artistically designed courtyards including the Royal Bath, which is adorned with the well-admired Golden Faucet among others. \n" +
                        "\n" +
                        "    Big Bell is another incredible artwork that attracts the visitors. Big enough to match its name, the bell was erected by Ranajit Malla, Bhaktapur’s last Malla King. It was used in those days for paying homage to Goddess Taleju, the lineage deity of Malla rulers, as well as to call assemblies of the citizens to discuss on given subjects concerning the state. Today, it is rung twice a day as a mark of tribute to the goddess. Right next to it is a smaller Barking Bell, to one’s surprise, all dogs around it start whining the moment it is rang by its caretaker.");
                break;



            case 4:
                temp.put("pic_path", "nyatapola-temple.jpg");
                temp.put("detail", "Nyatapola Temple – 5 storeyed temple presides over the  Taumadhi Square. Its country’s tallest pagoda temple. The struts, doors, windows and tympanums – each embellished with attractively carved divine figures – perfectly portrays the creative tradition of Newar craftsmen. The temple is dedicated to goddess Siddhi Laxmi, the manifestation of female force and creativity.Next to the Naytaponla Temple is the rectangular shaped Bhairavnath Temple. It houses a glided bust of Bhairav, the ferocious manifestation of Lord Shiva. The three-storied pagoda was razed to the grounds by the 1934 earthquake, and its latest renovation was undertaken by Bhaktapur Municipality in 1995 AD.");
                break;


            case 3:
                temp.put("pic_path","bhairav.jpg");
                temp.put("detail","This is another pagoda temple of lord Bhairab, the dreadful aspect of Lord Shiva. It stands near the Nyatapola temple and was originally constructed by King Jagat Jyoti Malla on a modest scale. It was later remodelled by King Bhupatindra Malla, a zealous lover of the arts, into what it is now a three-storeyed temple.");
                break;
            case 2:
                temp.put("pic_path", "peacock_window.jpg");
                temp.put("detail", "The Peacock Window, which is also called the \"Mona Lisa of Nepal\", is a rare masterpiece in wood. Dating back to the early 15th century, the unique latticed window has an intricately carved peacock in its center. The window adorns the Pujari Math which, with rows of exquisitely carved windows and doors, is equally appealing. The building presently houses the Woodcarving Museum. The museum has a rich collection of unique pieces in wood.");
                break;

            case 1:
                temp.put("pic_path", "tachupal-tole.jpg");
                temp.put("detail", "The Dattatreya temple is the main attraction of the square. Constructed by King Yaksha Malla, the giant three-storey temple is believed to have been built from a single tree. The Wane Layaku complex, which lies to the south-western corner of the Dattatreya temple, is noted for Bhaktapur’s second Taleju shrine. Enclosed with old houses, the courtyard sees throngs of people, especially during the Dashain festival, when a rare Ghau-batacha (water clock) is put on public display. During the Malla era, the water-clock was used by the then rulers and astrologers for fixing “propitious moments” for commencing and concluding various state and social ceremonies. The peacock window, which is also called the Mona Lisa of Nepal, is a rare masterpiece in wood.\n" +
                        "The window adorns the Pujari Math which, with rows of exquisitely carved windows and doors, is equally appealing. The building presently houses the Woodcarving Museum. The museum has a rich collection of unique pieces in wood. The Brass & Bronze Museum, housed in the historic Chikanpha Math, is the next highlight of the square. It has a wide collection of bronze and brassware’s including the ritual jars, utensils, water vessels, pots, spittoons and similar other household items. ");
                break;

        }

        return temp;
    }

}
