package creativeendlessgrowingceg.allergychecker;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Jonathan Alexander Norberg
 * @version 2018-01-23
 */

public class StartPageTip {
    public static String getTip(Context context){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(context.getString(R.string.holdCamera));
        arrayList.add(context.getString(R.string.foundABug));
        arrayList.add(context.getString(R.string.greatApp));
        arrayList.add(context.getString(R.string.shareWithFriend));
        arrayList.add(context.getString(R.string.wonderfulDay));
        return arrayList.get(new Random().nextInt(arrayList.size()));
    }

}
