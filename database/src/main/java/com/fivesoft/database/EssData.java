package com.fivesoft.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Simply save and read your app data.
 * <br><br>
 * This is more extended {@link SharedPreferences} library.
 * You can save here every {@link Serializable} object.
 * It also let you save lists easily and edit single items
 * with convenient interface.
 *
 */

@SuppressWarnings({"unchecked", "UnusedReturnValue", "unused", "deprecation"})
public class EssData {

    private final Context context;
    private final SharedPreferences sp;
    private String document = "APP_DATA_MAIN";

    //Awful, random prefix to avoid collisions with other shared_prefs
    //files from other libraries or the app.
    private static final String DOC_PREFIX = "03f8eojdgf74_";

    /**
     * Creates new AppDatabase instance.
     * @param context Non null context necessary to read and edit {@link SharedPreferences}
     * @return New AppDatabase instance.
     */

    public static EssData with(@NonNull Context context){
        return new EssData(context);
    }

    /**
     * Sets the default document so you needn't to write
     * it in every method while editing or reading database.
     * @param document The name of document.
     * @return Current AppDatabase instance.
     */

    public EssData setDocument(String document){
        this.document = document;
        return this;
    }

    /**
     * Overrides a field or creates a new one weather it doesn't exist.
     * @param document The name of document.
     * @param field The name of field.
     * @param value The value of a field.
     * @return Current AppDatabase instance.
     */

    public EssData set(String document, String field, Serializable value){
        return setInternal(document, field, value);
    }

    /**
     * Overrides a field or creates a new one weather it doesn't exist.
     * @param field The name of field.
     * @param value The value of a field.
     * @return Current AppDatabase instance.
     */

    public EssData set(String field, Serializable value){
        return setInternal(document, field, value);
    }

    /**
     * Overrides a field or creates a new one weather it doesn't exist.
     * WARNING!
     * If class isn't marked as {@link java.io.Serializable} it may be saved incorrectly.
     * Use {@link #set(String, String, Serializable)} instead to be sure your object will
     * be saved correctly.
     * @param document The name of document.
     * @param field The name of field.
     * @param value The value of a field.
     * @return Current AppDatabase instance.
     */

    @Deprecated
    public EssData set(String document, String field, Object value){
        return setInternal(document, field, value);
    }

    /**
     * Overrides a field or creates a new one weather it doesn't exist.
     * WARNING!
     * If class isn't marked as {@link java.io.Serializable} it may be saved incorrectly.
     * Use {@link #set(String, String, Serializable)} instead to be sure your object will
     * be saved correctly.
     * @param field The name of field.
     * @param value The value of a field.
     * @return Current AppDatabase instance.
     */

    @Deprecated
    public EssData set(String field, Object value){
        return setInternal(document, field, value);
    }

    /**
     * Saves bitmap in a given field.
     * @param document The name of document.
     * @param field The name of field.
     * @param value The bitmap you want to save.
     * @return Current AppDatabase instance.
     */

    public EssData set(String document, String field, Bitmap value){
        getData(document).edit().putString(field, toString(value)).apply();
        return this;
    }

    /**
     * Saves bitmap in a given field.
     * @param field The name of field.
     * @param value The bitmap you want to save.
     * @return Current AppDatabase instance.
     */

    public EssData set(String field, Bitmap value){
        set(document, field, value);
        return this;
    }


    /**
     * Adds new element to list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     * @param document The name of document.
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position where you want to add the element.
     *                 When you pass number lower than 0, your element will be inserted at 0 position.
     *                 When you pass number greater than (list size - 1) your element will be inserted at last position.
     * @return Current AppDatabase instance.
     */

    public EssData addToList(String document, String field, Serializable value, int position){
        ArrayList<Object> res = new ArrayList<>(getList(document, field));
        res.add(Math.max(0, Math.min(res.size(), position)), value);
        return set(document, field, res);
    }

    /**
     * Adds new element to list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position where you want to add the element.
     *                 When you pass number lower than 0, your element will be inserted at 0 position.
     *                 When you pass number greater than (list size - 1) your element will be inserted at last position.
     * @return Current AppDatabase instance.
     */

    public EssData addToList(String field, Serializable value, int position){
        return addToList(document, field, value, position);
    }

    /**
     * Replaces given element in the list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     *
     * Does nothing when:
     * <ul>
     *     <li>Position is < 0.</li>
     *     <li>Position is > (list size - 1)</li>
     * </ul>
     *
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position of the element you want to replace.
     * @return Current AppDatabase instance.
     */

    public EssData setInList(String document, String field, Serializable value, int position){
        ArrayList<Object> res = new ArrayList<>(getList(document, field));
        try{ res.set(position, value); } catch (Exception ignored){}
        return set(document, field, res);
    }

    /**
     * Replaces given element in the list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     *
     * Does nothing when:
     * <ul>
     *     <li>Position is < 0.</li>
     *     <li>Position is > (list size - 1)</li>
     * </ul>
     *
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position of the element you want to replace.
     * @return Current AppDatabase instance.
     */

    public EssData setInList(String field, Serializable value, int position){
        return setInList(document, field, value, position);
    }


    /**
     * Adds new element to list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     * WARNING!
     * If class isn't marked as {@link java.io.Serializable} it may be saved incorrectly.
     * Use {@link #addToList(String, String, Serializable, int)} instead to be sure your object will
     * be saved correctly.
     * @param document The name of document.
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position where you want to add the element.
     *                 When you pass number lower than 0, your element will be inserted at 0 position.
     *                 When you pass number greater than (list size - 1) your element will be inserted at last position.
     * @return Current AppDatabase instance.
     */

    @Deprecated
    public EssData addToList(String document, String field, Object value, int position){
        ArrayList<Object> res = new ArrayList<>(getList(document, field));
        res.add(Math.max(0, Math.min(res.size(), position)), value);
        return set(document, field, res);
    }

    /**
     * Adds new element to list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     * WARNING!
     * If class isn't marked as {@link java.io.Serializable} it may be saved incorrectly.
     * Use {@link #addToList(String, Serializable, int)} instead to be sure your object will
     * be saved correctly.
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position where you want to add the element.
     *                 When you pass number lower than 0, your element will be inserted at 0 position.
     *                 When you pass number greater than (list size - 1) your element will be inserted at last position.
     * @return Current AppDatabase instance.
     */

    @Deprecated
    public EssData addToList(String field, Object value, int position){
        return addToList(document, field, value, position);
    }

    /**
     * Replaces given element in the list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     *
     * Does nothing when:
     * <ul>
     *     <li>Position is < 0.</li>
     *     <li>Position is > (list size - 1)</li>
     * </ul>
     *
     * WARNING!
     * If class isn't marked as {@link java.io.Serializable} it may be saved incorrectly.
     * Use {@link #setInList(String, String, Serializable, int)} instead to be sure your object will
     * be saved correctly.
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position of the element you want to replace.
     * @return Current AppDatabase instance.
     */

    @Deprecated
    public EssData setInList(String document, String field, Object value, int position){
        ArrayList<Object> res = new ArrayList<>(getList(document, field));
        try{ res.set(position, value); } catch (Exception ignored){}
        return set(document, field, res);
    }

    /**
     * Replaces given element in the list at given field and document.
     * If field does't exist, creates a new list at the field
     * and then adds the element.
     * If field is not a list, overrides it with new list and
     * then adds the element.
     *
     * Does nothing when:
     * <ul>
     *     <li>Position is < 0.</li>
     *     <li>Position is > (list size - 1)</li>
     * </ul>
     *
     * WARNING!
     * If class isn't marked as {@link java.io.Serializable} it may be saved incorrectly.
     * Use {@link #setInList(String, Serializable, int)} (String, String, Serializable)} instead to be sure your object will
     * be saved correctly.
     * @param field The name of field.
     * @param value The value of a field.
     * @param position The position of the element you want to replace.
     * @return Current AppDatabase instance.
     */

    @Deprecated
    public EssData setInList(String field, Object value, int position){
        return setInList(document, field, value, position);
    }

    /**
     * Removes given field from given document.
     * @param document The name of document.
     * @param field The name of field.
     * @return Current AppDatabase instance.
     */

    public EssData remove(String document, String field){
        getData(document).edit().remove(field).apply();
        return this;
    }

    /**
     * Removes given field from given document.
     * @param field The name of field.
     * @return Current AppDatabase instance.
     */

    public EssData remove(String field){
        return remove(document, field);
    }

    /**
     * Removes all fields in a document.
     * @param document The document you want to clear.
     * @return Current AppDatabase instance.
     */

    public EssData clear(String document){
        File dir = new File(context.getFilesDir().getParent() + "/shared_prefs/");
        boolean res = new File(dir, DOC_PREFIX.concat(document).concat(".xml")).delete();
        return this;
    }


    /**
     * Removes all fields in a document.
     * @return Current AppDatabase instance.
     */

    public EssData clearAll(){
        for(String document: getDocumentsInternal()){
            clear(document);
        }
        return this;
    }

    /**
     * Removes given position from list at given field and document.
     * Does nothing when:
     * <ul>
     *     <li>Field is not a list.</li>
     *     <li>Field doesn't exist.</li>
     *     <li>List doesn't contain a position you are passing.</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @param position The position of the element you want to remove.
     * @return Current AppDatabase instance.
     */

    public EssData removeFromList(String document, String field, int position){
        ArrayList<Object> res = new ArrayList<>(getList(document, field));
        try{
            res.remove(position);
            return set(document, field, res);
        } catch (Exception e){
            return this;
        }
    }

    /**
     * Removes given position from list at given field and document.
     * Does nothing when:
     * <ul>
     *     <li>Field is not a list.</li>
     *     <li>Field doesn't exist.</li>
     *     <li>List doesn't contain a position you are passing.</li>
     * </ul>
     * @param field The name of field.
     * @param position The position of the element you want to remove.
     * @return Current AppDatabase instance.
     */

    public EssData removeFromList(String field, int position){
        return removeFromList(document, field, position);
    }

    /**
     * Returns an element from the list at given field and document.
     * Returns null if:
     * <ul>
     *     <li>Field doesn't exist.</li>
     *     <li>Field isn't a list</li>
     *     <li>List doesn't contain given position.</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @param position The position of the element you want to remove.
     * @return Current AppDatabase instance.
     */

    @Nullable
    public <T> T getFromList(String document, String field, int position){
        try {
            return (T) new ArrayList<>(getList(document, field)).get(position);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Returns an element from the list at given field and document.
     * Returns null if:
     * <ul>
     *     <li>Field doesn't exist.</li>
     *     <li>Field isn't a list</li>
     *     <li>List doesn't contain given position.</li>
     * </ul>
     * @param field The name of field.
     * @param position The position of the element you want to remove.
     * @return Current AppDatabase instance.
     */

    @Nullable
    public <T> T getFromList(String field, int position){
        return getFromList(document, field, position);
    }

    /**
     * Returns field value.
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    @Nullable
    public <T> T get(String document, String field){

        if(document == null || field == null)
            return null;

        try { return new Gson().fromJson(
                getData(document).getString(field, null), new TypeToken<Object>() {}.getType());
        } catch (Exception e){ return null; }
    }

    /**
     * Returns field value.
     * @param field The name of field.
     * @return Field value.
     */

    @Nullable
    public <T> T get(String field){
        return get(document, field);
    }

    /**
     * Returns field value.
     * Returns default value (null) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a Bitmap</li>
     * </ul>
     * @param field The name of field.
     * @param document The name of document.
     * @return Field value.
     */

    @Nullable
    public Bitmap getBitmap(String document, String field){
        return toBitmap(getString(document, field));
    }

    /**
     * Returns field value.
     * Returns default value (null) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a Bitmap</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    @Nullable
    public Bitmap getBitmap(String field){
        return toBitmap(getString(document, field));
    }

    /**
     * Returns field value.
     * Returns default value (false) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a boolean</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    public boolean getBoolean(String document, String field){
        return getData(document).getBoolean(field, false);
    }

    /**
     * Returns field value.
     * Returns default value (false) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a boolean</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    public boolean getBoolean(String field){
        return getBoolean(document, field);
    }


    /**
     * Returns field value.
     * Returns default value (null) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a String</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    @Nullable
    public String getString(String document, String field){
        return getData(document).getString(field, null);
    }

    /**
     * Returns field value.
     * Returns default value (null) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a String</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    @Nullable
    public String getString(String field){
        return getString(document, field);
    }

    /**
     * Returns field value.
     * Returns default value ({@link Integer#MIN_VALUE}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not an Integer</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    public int getInt(String document, String field){
        return getData(document).getInt(field, Integer.MIN_VALUE);
    }

    /**
     * Returns field value.
     * Returns default value ({@link Integer#MIN_VALUE}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not an Integer</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    public int getInt(String field){
        return getInt(document, field);
    }

    /**
     * Returns field value.
     * Returns default value ({@link Float#MIN_VALUE}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a Float</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    public float getFloat(String document, String field){
        return getData(document).getFloat(field, Float.MIN_VALUE);
    }

    /**
     * Returns field value.
     * Returns default value ({@link Float#MIN_VALUE}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a Float</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    public float getFloat(String field){
        return getFloat(document, field);
    }


    /**
     * Returns field value.
     * Returns default value ({@link Long#MIN_VALUE}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a Long</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    public long getLong(String document, String field){
        return getData(document).getLong(field, Long.MIN_VALUE);
    }

    /**
     * Returns field value.
     * Returns default value ({@link Long#MIN_VALUE}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a Long</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    public long getLong(String field){
        return getLong(document, field);
    }

    /**
     * Returns field value.
     * Returns default value (empty {@link ArrayList}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a list</li>
     * </ul>
     * @param document The name of document.
     * @param field The name of field.
     * @return Field value.
     */

    @NonNull
    public <T extends List<?>> T getList(String document, String field){
        try{
            T res;
            res = new Gson().fromJson(getData(document).getString(field, "[]"), new TypeToken<List<Object>>(){}.getType());
            if(res == null) {
                return (T) new ArrayList<>();
            } else {
                return res;
            }
        } catch (Exception e){
            return (T) new ArrayList<>();
        }
    }

    /**
     * Returns field value.
     * Returns default value (empty {@link ArrayList}) if:
     * <ul>
     *     <li>Filed doesn't exist</li>
     *     <li>Filed is not a list</li>
     * </ul>
     * @param field The name of field.
     * @return Field value.
     */

    @NonNull
    public <T extends List<?>> T getList(String field){
        return getList(document, field);
    }


    /**
     * Returns all documents names.
     * @return All documents names.
     */

    public List<String> getDocuments(){
        return getDocumentsInternal();
    }

    /**
     * Returns all field names located in given document.
     * @param document The name of document.
     * @return All field names from document.
     */

    public List<String> getFields(String document){
        return new ArrayList<>(getData(document).getAll().keySet());
    }

    /**
     * Returns all field names located in given document.
     * @return All field names from document.
     */

    public List<String> getFields(){
        return new ArrayList<>(getData(document).getAll().keySet());
    }



    //Private methods

    private SharedPreferences getData(String document){
        if(!document.equals(this.document)) {
            return context.getSharedPreferences(DOC_PREFIX.concat(document), Context.MODE_PRIVATE);
        } else {
            return sp;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private List<String> getDocumentsInternal(){

        List<String> res = new ArrayList<>();

        File prefsdir = new File(context.getApplicationInfo().dataDir,"shared_prefs");
        if(prefsdir.exists() && prefsdir.isDirectory()){
            if(prefsdir.list() != null)
                for(String document: prefsdir.list())
                    if(document.startsWith(DOC_PREFIX) && !document.equals(DOC_PREFIX.concat(".xml")))
                        res.add(document.substring(DOC_PREFIX.length(), document.length() - 4));
        }
        return res;
    }

    private EssData setInternal(String document, String field, Object value){

        if(document == null || field == null)
            return this;

        getData(document).edit().putString(field, new Gson().toJson(value)).apply();
        return this;
    }

    private EssData(Context context){
        this.context = context;
        sp = context.getSharedPreferences(document, Context.MODE_PRIVATE);
    }

    private static String toString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
        byte [] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static Bitmap toBitmap(String encodedString){
        try {
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            return null;
        }
    }
}
