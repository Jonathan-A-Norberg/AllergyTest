package creativeendlessgrowingceg.allergychecker;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * load an UI.
 * Created by Enathen on 2018-01-12.
 */

public class LoadUIAllergies {
    private static final String TAG = "LoadUIAllergies";
    protected StartPage context;
    protected FrameLayout parentFrameLayout;
    protected LinearLayout parentLinearLayout;
    protected TreeMap<Integer, TreeMap<Integer, LinearLayout>> linearLayoutToInsertLater = new TreeMap<>();
    protected TreeMap<Integer, Integer> parentKeyToParentImage = new TreeMap<>();
    protected TreeMap<Integer, LinearLayout> parentLinearLayoutHashMap = new TreeMap<>();
    private boolean preference;
    private LayoutInflater inflater;
    private TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> myAllergies;
    private TreeMap<String, AllergyCheckBoxClass> allergyInfo = new TreeMap<>();


    public LoadUIAllergies(boolean preference, LayoutInflater inflater, StartPage context, FrameLayout parentFrameLayout, LinearLayout parentLinearLayout, TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> myAllergies) {
        this.preference = preference;
        this.inflater = inflater;
        this.context = context;

        this.parentFrameLayout = parentFrameLayout;
        this.parentLinearLayout = parentLinearLayout;
        this.myAllergies = myAllergies;
        new AddCategories().execute();
    }

    public LoadUIAllergies() {

    }

    /**
     * add all categories
     *
     * @param parentKey          to set.
     * @param parentKeyToPicture
     */
    private void addCategory(int parentKey, CheckBox parentCheckBox, int parentKeyToPicture) {
        TreeMap<Integer, LinearLayout> categoriesLinearLayout = new TreeMap<>();

        ArrayList<AllergyCheckBoxClass> allergyCheckBoxClasses = new ArrayList<>();
        for (AllergyList.PictureIngredient pictureIngredient : myAllergies.get(parentKey)) {

            LinearLayout newLinearLayout = (LinearLayout) inflater.inflate(R.layout.leftmarginrowlayout, null);
            TextView textOfAllergy = (TextView) newLinearLayout.findViewById(R.id.textViewLeftMargin);
            ImageView imageOfAllergy = (ImageView) newLinearLayout.findViewById(R.id.imageViewLeftMargin);
            CheckBox checkBoxOfAllergy = (CheckBox) newLinearLayout.findViewById(R.id.checkBoxRowLeftMargin);

            textOfAllergy.setText(TextHandler.capitalLetter(pictureIngredient.getIngredient()));
            imageOfAllergy.setImageResource(pictureIngredient.getPicture());
            categoriesLinearLayout.put(pictureIngredient.getId(), newLinearLayout);
            if (allergyInfo.containsKey(pictureIngredient.getIngredient())) {
                AllergyCheckBoxClass allergyCheckBoxClass = new AllergyCheckBoxClass(checkBoxOfAllergy, parentCheckBox,
                        parentKeyToPicture, pictureIngredient.getId(), parentKey, pictureIngredient.getIngredient(),
                        allergyInfo.get(pictureIngredient.getIngredient()).getSameItemDifferentCategories());
                allergyInfo.get(pictureIngredient.getIngredient()).getSameItemDifferentCategories().add(allergyCheckBoxClass);
                allergyCheckBoxClasses.add(allergyCheckBoxClass);

            } else {
                allergyInfo.put(pictureIngredient.getIngredient(), new AllergyCheckBoxClass(checkBoxOfAllergy,
                        parentCheckBox, parentKeyToPicture, pictureIngredient.getId(), parentKey,
                        pictureIngredient.getIngredient()));
                allergyInfo.get(pictureIngredient.getIngredient()).getSameItemDifferentCategories().add(allergyInfo.get(pictureIngredient.getIngredient()));
                allergyCheckBoxClasses.add(allergyInfo.get(pictureIngredient.getIngredient()));
            }


        }
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyCheckBoxClasses) {
            allergyCheckBoxClass.setNeigbhourClasses(allergyCheckBoxClasses);

        }
        linearLayoutToInsertLater.put(parentKey, categoriesLinearLayout);


    }

    private CheckBox createParentLinearLayout(final int key, int pictureID) {
        final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.rowcategorylayout, null);
        LinearLayout linearLayoutRow = (LinearLayout) linearLayout.findViewById(R.id.linearLayoutRowCategoryHorizontal);
        ((TextView) linearLayout.findViewById(R.id.textViewCategory)).setText(TextHandler.capitalLetter(context.getString(key)));

        final CheckBox checkboxRowCategory = (CheckBox) linearLayoutRow.findViewById(R.id.checkBoxRowCategory);


        ImageView imageIcon = (ImageView) linearLayoutRow.findViewById(R.id.imageViewRowCategory);
        imageIcon.setImageResource(pictureID);
        final ImageView dropDownImage = (ImageView) linearLayoutRow.findViewById(R.id.dropDownList);

        dropDownImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLinearLayout.onclickDropDownList(dropDownImage, linearLayoutToInsertLater.get(key), linearLayout);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLinearLayout.onclickDropDownList(dropDownImage, linearLayoutToInsertLater.get(key), linearLayout);
            }
        });

        parentLinearLayoutHashMap.put(key, linearLayout);

        return checkboxRowCategory;

    }

    public void savePicture(StartPage context, HashMap<Integer, ImageView> imageViewHashMap) {
        String alreadyString = "00000000";
        ArrayList<Integer> alreadySelectedImages = new ArrayList<>();
        int i = 0;
        for (ImageView imageView : imageViewHashMap.values()) {
            imageView.setImageDrawable(context.getDrawable(R.drawable.emptyborder));
        }
        HashSet<Integer> allergySavePicture = SharedPreferenceClass.getSharedPreference(context, "allergySavePicture", TAG);
        allergySavePicture.addAll(SharedPreferenceClass.getSharedPreference(context, "preferenceSavePicture", TAG));
        HashSet<String> allergySavePictureNAME = (HashSet<String>) SharedPreferenceClass.getSharedPreferenceString(context, "allergySavePictureName", TAG);
        allergySavePictureNAME.addAll(SharedPreferenceClass.getSharedPreferenceString(context, "preferenceSavePictureName", TAG));
        HashSet<Integer> allergyToRemove = new HashSet<>();
        for (Integer integer : allergySavePicture) {
            int checkExistence = context.getResources().getIdentifier(String.valueOf(integer), "drawable", context.getPackageName());
            if (checkExistence != 0) {
                if (!allergySavePictureNAME.contains(context.getResources().getResourceEntryName(integer))) {
                    allergyToRemove.add(integer);
                    Log.d(TAG, "savePictureREMOVE: "+ checkExistence + " : " + integer);
                }

            }
            else{
                Log.d(TAG, "savePictureREMOVE: "+ checkExistence + " : " + integer);
                allergyToRemove.remove(integer);
            }
        }
        allergySavePicture.removeAll(allergyToRemove);
        for (int id : allergySavePicture) {
            if (i > 7) {
                break;
            }

            if (alreadyString.charAt(i) == '0') {
                if (!alreadySelectedImages.contains(id)) {

                    imageViewHashMap.get(i).setImageResource(id);
                    alreadySelectedImages.add(id);
                    StringBuilder myName = new StringBuilder(alreadyString);
                    myName.setCharAt(i, '1');
                    alreadyString = myName.toString();
                    i++;
                }
            }
        }
    }

    public HashSet<String> getCurrentlyActiveAllergies() {
        HashSet<String> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                if (checkBoxClass.isOn()) {
                    hashSet.add(checkBoxClass.getChildIngredient());
                }
            }
        }
        return hashSet;
    }

    public HashSet<String> getCurrentlyNotActiveAllergies() {
        HashSet<String> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                if (!checkBoxClass.isOn()) {
                    hashSet.add(checkBoxClass.getChildIngredient());
                }
            }
        }
        return hashSet;
    }

    public HashSet<Integer> getCurrentlyActiveParentAllergies() {
        HashSet<Integer> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            if (allergyCheckBoxClass.getParentCheckBox().isChecked()) {
                hashSet.add(allergyCheckBoxClass.getParentKey());


            }
        }
        return hashSet;
    }

    public HashSet<Integer> getCurrentlyNotActiveParentAllergies() {
        HashSet<Integer> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            if (!allergyCheckBoxClass.getParentCheckBox().isChecked()) {
                hashSet.add(allergyCheckBoxClass.getParentKey());
            }
        }
        return hashSet;
    }

    public HashMap<String, CheckBox> getCheckBoxToRemove() {
        HashMap<String, CheckBox> hashMap = new HashMap<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            if (allergyCheckBoxClass.isRemove()) {
                hashMap.put(allergyCheckBoxClass.getChildIngredient(), allergyCheckBoxClass.getChildCheckBox());
            }
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {

                if (checkBoxClass.isRemove()) {
                    hashMap.put(checkBoxClass.getChildIngredient(), checkBoxClass.getChildCheckBox());
                }

            }
        }
        return hashMap;
    }

    public void saveCurrentlyActive(boolean preference) {
        for (String key : getCheckBoxToRemove().keySet()) {
            SharedPreferenceClass.setBoolean(key, context, false);
        }

        for (String key : getCurrentlyActiveAllergies()) {
            SharedPreferenceClass.setBoolean(key, context, true);
        }

        if (!preference) {
            SharedPreferenceClass.setSharedPreference(context, getCurrentlyActiveParentAllergies(), "allergyToSendToPreference", TAG);
            SharedPreferenceClass.setSharedPreference(context, getCurrentlyNotActiveParentAllergies(), "notAllergyToSendToPreference", TAG);
        }
        if (preference) {

            SharedPreferenceClass.setSharedPreference(context, getCurrentlyActiveAllergies(), "preferenceToSendToAllergy", TAG);
            SharedPreferenceClass.setSharedPreference(context, getCurrentlyNotActiveAllergies(), "notPreferenceToSendToAllergy", TAG);

        }
        saveAllergies();

    }

    private void saveAllergies() {
        HashSet<Integer> hashSet = new HashSet<>();
        HashSet<Integer> hashSetPicture = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                if (!preference) {
                    if (checkBoxClass.isOn()) {
                        hashSet.add(checkBoxClass.getChildKey());
                        hashSetPicture.add(checkBoxClass.getParentPicture());
                    }
                } else {
                    if (checkBoxClass.getParentCheckBox().isChecked()) {
                        hashSetPicture.add(checkBoxClass.getParentPicture());
                    }
                    if (checkBoxClass.isOn()) {
                        hashSet.add(checkBoxClass.getChildKey());
                    }
                }

            }
        }
        for (Integer integer : hashSet) {
            Log.d(TAG, "HASHSET: " + context.getString(integer));
        }
        HashSet<Integer> allergiesToSave = SharedPreferenceClass.getSharedPreference(context, "allergiesToSave", TAG);
        if (!preference) {
            for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                    if (checkBoxClass.getParentCheckBox().isChecked()) {
                        hashSet.add(checkBoxClass.getParentKey());
                        for (AllergyCheckBoxClass boxClass : checkBoxClass.getNeigbhourClasses()) {
                            allergiesToSave.remove(boxClass.getChildKey());
                        }
                    }
                }
            }
            for (Integer integer : hashSet) {
                Log.d(TAG, "HASHSET: " + context.getString(integer));
            }
            SharedPreferenceClass.setSharedPreference(context, hashSet, "allergySave", TAG);
            SharedPreferenceClass.setSharedPreference(context, hashSetPicture, "allergySavePicture", TAG);
            HashSet<String> hashSetPictureName = new HashSet<>();
            for (Integer integer : hashSetPicture) {
                hashSetPictureName.add(context.getResources().getResourceEntryName(integer));
            }
            SharedPreferenceClass.setSharedPreference(context, allergiesToSave, "allergiesToSave", TAG);
            SharedPreferenceClass.setSharedPreference(context, hashSetPictureName, "allergySavePictureName", TAG);
        } else {

            HashSet<Integer> allergySave = SharedPreferenceClass.getSharedPreference(context, "allergySave", TAG);
            ArrayList<Integer> parentKeys = AllergyList.getParentKeys();

            for (Integer integer : allergySave) {
                Log.d(TAG, "ALLERGIES: " + context.getString(integer));
            }
            //hashSet.addAll(allergySave);
            boolean open = false;

            for (Integer parentKey : parentKeys) {
                Log.d(TAG, "parentkey: " + allergySave.contains(parentKey) + " : " + hashSet.contains(parentKey) + " : " + context.getString(parentKey));
                if (!allergySave.contains(parentKey) && hashSet.contains(parentKey)) {
                    Log.d(TAG, "Allergy does not Contains: " + context.getString(parentKey));
                    ArrayList<AllergyList.PictureIngredient> specifiedKey = new AllergyList(context).getSpecifiedKey(parentKey);
                    for (AllergyList.PictureIngredient pictureIngredient : specifiedKey) {
                        if(!allergiesToSave.contains(pictureIngredient.getId())){
                            hashSet.add(pictureIngredient.getId());
                            Log.d(TAG, "saveAllergies: " + pictureIngredient.getIngredient());
                        }
                    }
                    open = true;
                    hashSetPicture.add(specifiedKey.get(0).getPicture());
                } else if (allergySave.contains(parentKey) && !hashSet.contains(parentKey)) {
                    ArrayList<AllergyList.PictureIngredient> specifiedKey = new AllergyList(context).getSpecifiedKey(parentKey);
                    for (AllergyList.PictureIngredient pictureIngredient : specifiedKey) {
                        if (!allergySave.contains(pictureIngredient.getId())) {
                            Log.d(TAG, "ALLERGYSAVE THIS : " + pictureIngredient.getIngredient());
                            allergiesToSave.add(pictureIngredient.getId());
                        }
                        hashSet.remove(pictureIngredient.getId());
                        Log.d(TAG, "REMOVE: " + pictureIngredient.getIngredient());
                    }
                    hashSetPicture.remove(specifiedKey.get(0).getPicture());
                    open = true;
                    hashSet.remove(parentKey);
                    Log.d(TAG, "REMOVE: " + context.getString(parentKey));
                }

            }
            if (open) {
                SharedPreferenceClass.setSharedPreference(context, allergiesToSave, "allergiesToSave", TAG);
                SharedPreferenceClass.setSharedPreference(context, hashSet, "allergySave", TAG);
                SharedPreferenceClass.setSharedPreference(context, hashSetPicture, "allergySavePicture", TAG);
                HashSet<String> hashSetPictureName = new HashSet<>();
                for (Integer integer : hashSetPicture) {
                    hashSetPictureName.add(context.getResources().getResourceEntryName(integer));
                }
                SharedPreferenceClass.setSharedPreference(context, hashSetPictureName, "preferenceSavePictureName", TAG);
            }

        }

    }

    synchronized public HashSet<Integer> getAllergies(StartPage startPage) {
        HashSet<Integer> set = SharedPreferenceClass.getSharedPreference(startPage, "allergySave", "LoadUIAllergies");
        set.addAll(SharedPreferenceClass.getSharedPreference(startPage, "preferenceSave", "LoadUIAllergies"));
        return set;
    }

    synchronized public HashSet<Integer> getAllergies(ShowAllergies showAllergies) {
        HashSet<Integer> set = SharedPreferenceClass.getSharedPreference(showAllergies.getContext(), "allergySave", "LoadUIAllergies");
        set.addAll(SharedPreferenceClass.getSharedPreference(showAllergies.getContext(), "preferenceSave", "LoadUIAllergies"));
        return set;
    }

    private class AddCategories extends AsyncTask<String, Integer, String> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            for (int key : myAllergies.keySet()) {
                parentKeyToParentImage.put(key, myAllergies.get(key).get(0).getPicture());
            }
            for (int key : myAllergies.keySet()) {

                addCategory(key, createParentLinearLayout(key, parentKeyToParentImage.get(key)), parentKeyToParentImage.get(key));
            }
            return null;
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String s) {
            for (int key : linearLayoutToInsertLater.keySet()) {

                parentLinearLayout.addView(parentLinearLayoutHashMap.get(key));
            }

            for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                    final CheckBox checkBox = checkBoxClass.getChildCheckBox();
                    checkBox.setChecked(SharedPreferenceClass.checkBoolean(checkBoxClass.getChildIngredient(), context));
                    checkBoxClass.setOn(SharedPreferenceClass.checkBoolean(checkBoxClass.getChildIngredient(), context));
                    if (!preference) {
                        if (checkBox.isChecked()) {
                            checkBoxClass.getParentCheckBox().setChecked(true);
                            checkBoxClass.setOn(true);
                        }
                        CreateLinearLayout.checkBoxChildOnCheckedListener(checkBoxClass);
                    } else {
                        CreateLinearLayout.checkBoxChildOnCheckedListenerPreference(checkBoxClass);

                    }

                }
            }
            HashSet<Integer> hashSet = new HashSet<>();

            for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                if (!hashSet.contains(allergyCheckBoxClass.getParentKey())) {
                    hashSet.add(allergyCheckBoxClass.getParentKey());
                    CreateLinearLayout.parentCheckedChanged(allergyCheckBoxClass);
                    if (preference) {
                        for (AllergyCheckBoxClass allergyCheckBoxClass1 : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                            CreateLinearLayout.checkParentShouldChecked(allergyCheckBoxClass1, true);
                        }

                    }

                }
            }
            if (!preference) {
                HashSet<String> hashSetCheckedAllergies = (HashSet<String>) SharedPreferenceClass.getSharedPreferenceString(context, "preferenceToSendToAllergy", TAG);
                HashSet<String> hashSetCheckedNotAllergies = (HashSet<String>) SharedPreferenceClass.getSharedPreferenceString(context, "notPreferenceToSendToAllergy", TAG);
                HashSet<Integer> alreadyChecked = new HashSet<>();
                for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                    if (!alreadyChecked.contains(allergyCheckBoxClass.getParentKey())) {
                        if (hashSetCheckedAllergies.contains(context.getString(allergyCheckBoxClass.getParentKey()))) {
                            alreadyChecked.add(allergyCheckBoxClass.getParentKey());
                            Log.d(TAG, "lägger till från preference: " + context.getString(allergyCheckBoxClass.getParentKey()));
                            allergyCheckBoxClass.getParentCheckBox().setChecked(true);
                        }
                        if (hashSetCheckedNotAllergies.contains(context.getString(allergyCheckBoxClass.getParentKey()))) {
                            alreadyChecked.add(allergyCheckBoxClass.getParentKey());
                            Log.d(TAG, "TAR bort från preference: " + context.getString(allergyCheckBoxClass.getParentKey()));
                            allergyCheckBoxClass.getParentCheckBox().setChecked(false);
                        }
                    }

                }

                hashSetCheckedAllergies.clear();
                hashSetCheckedNotAllergies.clear();
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "preferenceToSendToAllergy", TAG);
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "notPreferenceToSendToAllergy", TAG);
            } else {
                HashSet<Integer> hashSetCheckedAllergies = SharedPreferenceClass.getSharedPreference(context, "allergyToSendToPreference", TAG);
                HashSet<Integer> hashSetCheckedNotAllergies = SharedPreferenceClass.getSharedPreference(context, "notAllergyToSendToPreference", TAG);

                for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                    if (hashSetCheckedAllergies.contains(allergyCheckBoxClass.getChildKey())) {
                        Log.d(TAG, "lägger till från allergy: " + context.getString(allergyCheckBoxClass.getParentKey()));
                        allergyCheckBoxClass.getChildCheckBox().setChecked(true);
                    }
                    if (hashSetCheckedNotAllergies.contains(allergyCheckBoxClass.getChildKey())) {
                        Log.d(TAG, "TAR bort från allergy: " + context.getString(allergyCheckBoxClass.getParentKey()));
                        allergyCheckBoxClass.getChildCheckBox().setChecked(false);
                    }

                }
                hashSetCheckedAllergies.clear();
                hashSetCheckedNotAllergies.clear();
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "allergyToSendToPreference", TAG);
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "notAllergyToSendToPreference", TAG);
            }
            parentLinearLayout.addView(new TextView(context));
            parentLinearLayout.addView(new TextView(context));
            parentLinearLayout.addView(new TextView(context));
            parentLinearLayout.addView(new TextView(context));


            parentLinearLayout.removeView(parentLinearLayout.findViewById(R.id.progressBarAllergy));

            super.onPostExecute(s);
        }
    }


}