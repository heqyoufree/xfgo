package com.locbytes.xfgo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class xfgo implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if( lpparam.packageName.equals("com.bilibili.fatego")
                || lpparam.packageName.equals("com.tencent.tmgp.fgo")
                || lpparam.packageName.contains("com.bilibili.fgo.") ){

            findAndHookMethod("com.netease.htprotect.HTProtect",lpparam.classLoader,"getDataSign", Context.class, String.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if(param.args[1].toString().contains("battleresult")){

                        Boolean battleCancel = false;
                        if( getOptions("main") && getOptions("battleCancel") ){
                            battleCancel = true;
                        }
                        if(battleCancel){

                            //XposedBridge.log("Battle Result");
                            String requestData=param.args[1].toString();
                            String[] dataArray=requestData.split("&");
                            /*
                            int i=0;
                            for (String aDataArray : dataArray) {
                                XposedBridge.log(i+" "+aDataArray);
                                ++i;
                            }
                            */

                            // result
                            //XposedBridge.log(dataArray[12]);

                            // 获取用户ID，方便请求随机数
                            String userId=dataArray[19].substring(7);

                            JSONObject jsonObject = new JSONObject(dataArray[12].substring(7));

                            StringBuilder newRequestData = new StringBuilder();

                            if(jsonObject.getInt("battleResult")==3){
                                battleCancel=true;
                                // battleResult=1 任务完成
                                jsonObject.put("battleResult",1);
                                // elapsedTurn=11 回合数11
                                jsonObject.put("elapsedTurn",getRandomNumFromProxyServer(userId));
                                // 因JSONArray类要求，只能将最低Android SDK Version由API-17为API-19
                                JSONArray aliveUniqueIds = jsonObject.getJSONArray("aliveUniqueIds");
                                // aliveUniqueIds=[] 敌方无存活
                                aliveUniqueIds = new JSONArray();
                                jsonObject.put("aliveUniqueIds",aliveUniqueIds);
                                //XposedBridge.log(jsonObject.toString());
                                dataArray[12]="result="+jsonObject.toString();
                                //XposedBridge.log(dataArray[12]);
                                int i=1;
                                for (String aDataArray : dataArray) {
                                    newRequestData.append(aDataArray);
                                    if(i<dataArray.length){
                                        newRequestData.append("&");
                                    }
                                    ++i;
                                }

                                param.args[1]=newRequestData.toString();
                            }

                        }

                    }
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //XposedBridge.log(param.getResult().toString());
                    if(param.args[1].toString().contains("battle_setup")||param.args[1].toString().contains("battle_resume")){
                        //XposedBridge.log("Battle Setup/Resume");
                        //XposedBridge.log(param.getResult().toString());
                        if(getOptions("main")){
                            param.setResult("");
                        }
                    }
                    super.afterHookedMethod(param);
                }
            });

        }
    }

    // 从服务器获取随机数，若不能获取则返回11，不设置服务器地址也返回11
    private int getRandomNumFromProxyServer(String userId) {
        String serverAddress = "http://com.locbytes.xfgo.signal/";
        String url = serverAddress + "?" +userId;
        int randomNumber = 11;
        try {
            randomNumber = Integer.parseInt(HttpUtil.get(url));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return randomNumber;
    }

    private Boolean getOptions(String key) {
        JSONObject options;
        Boolean option = false;
        try {
             options = new JSONObject(FileUtil.getFileDataFromSdcard("options"));
             option = options.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return option;
    }

}
