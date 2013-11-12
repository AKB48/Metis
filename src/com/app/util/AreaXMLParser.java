package com.app.util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class AreaXMLParser {
    public static ArrayList<State> doParse(Activity act)throws XmlPullParserException, IOException {

        XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        XmlPullParser parser = xmlFactory.newPullParser();
        AssetManager am = act.getAssets();
        InputStream inStream = am.open("area.plist");
        parser.setInput(inStream, "utf-8");
        int evtType = parser.getEventType();
        boolean parsingCity = false;
        boolean parsingArea = false;
        ArrayList<State>stateList = new ArrayList<State>();
        ArrayList<City> cityList = null;
        ArrayList<Area>areaList = null;
        State stInfo;
        City ctInfo;
        Area arInfo;
        while (evtType != XmlPullParser.END_DOCUMENT){
            switch (evtType){
                case XmlPullParser.START_DOCUMENT:{
                    break;
                }
                case XmlPullParser.START_TAG:{

                    if (parser.getName().equals("key")){
                        String keyText =  parser.nextText();
                        if (keyText.equals("cities")){
                            if (!parsingCity){
                                cityList = new ArrayList<City>();
                                parsingCity = true;
                            }


                        } else if (keyText.equals("areas")){
                            if (!parsingArea){
                                areaList = new ArrayList<Area>();
                                parsingArea = true;
                            }

                        } else if (keyText.equals("city")) {
                            parser.nextTag();
                            ctInfo = new City();
                            ctInfo.setName(parser.nextText());
//                            Log.d("XML Demo City Name", ctInfo.getName());
                            parser.nextTag();
                            parser.nextText();
                            parser.nextTag();
                            //parser.nextTag();
                            //parser.nextTag();
                            String strLat = parser.nextText();
                            if (strLat != null && strLat.length()!=0){
                                ctInfo.setLatitude(Double.parseDouble(strLat));
                            }
                            parser.nextTag();
                            parser.nextText();
                            parser.nextTag();
                            String strLong = parser.nextText();
                            if (strLong != null && strLong.length()!=0){
                                ctInfo.setLongitude(Double.parseDouble(strLong));
                            }
                            if (cityList != null){
                                cityList.add(ctInfo);
//                                Log.d("XML Demo City Name", "Add to city list!");
                            }
                            ctInfo.setAreaList(areaList);
                            parsingArea = false;
                        } else if (keyText.equals("state")) {
                            parsingCity = false;
                            parser.nextTag();
                            stInfo = new State();
                            stInfo.setName(parser.nextText());
                            stInfo.setAreaList(cityList);
                            stateList.add(stInfo);
//                            Log.d("XML Demo StateName", stInfo.getName());
                        } else if (keyText.equals("latitude") && parsingArea){
                            arInfo = new Area();
                            parser.nextTag();
                            String strLat = parser.nextText();
                            if (strLat != null && strLat.length()!=0){
                                arInfo.setLatitude(Double.parseDouble(strLat));
                            }
                            parser.nextTag();
                            parser.nextText();
                            parser.nextTag();
                            String strLong = parser.nextText();
                            if (strLong != null && strLong.length()==0){
                                arInfo.setLongitude(Double.parseDouble(strLong));
                            }
                            parser.nextTag();
                            parser.nextText();
                            parser.nextTag();
                            arInfo.setName(parser.nextText());
//                            Log.d("XML Demo Area Name", arInfo.getName());
                            areaList.add(arInfo);
                        }

                        break;
                    }

                    break;
                }
                case XmlPullParser.TEXT:{
                    break;
                }
                case XmlPullParser.END_TAG:{

                    break;
                }
                default:
                    break;
            }
            evtType = parser.next();
        }
        
        return stateList;
    }
}
