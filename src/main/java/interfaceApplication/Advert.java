package interfaceApplication;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Model.CommonModel;
import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.authority.plvDef.plvType;
import common.java.interfaceModel.GrapeDBSpecField;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.nlogger.nlogger;
import common.java.session.session;
import common.java.string.StringHelper;

/**
 * 广告管理
 * 
 *
 */
public class Advert {
    private GrapeTreeDBModel advert;
    private GrapeDBSpecField gDbSpecField;
    private CommonModel model;
    private session se;
    private JSONObject userInfo = null;
    private String currentWeb = null;
    private Integer userType = 0;

    public Advert() {
        model = new CommonModel();

        advert = new GrapeTreeDBModel();

        gDbSpecField = new GrapeDBSpecField();
        gDbSpecField.importDescription(appsProxy.tableConfig("Advert"));
        advert.descriptionModel(gDbSpecField);

        se = new session();
        userInfo = se.getDatas();
        if (userInfo != null && userInfo.size() != 0) {
            currentWeb = userInfo.getString("currentWeb"); // 当前用户所属网站id
            userType = userInfo.getInt("userType");// 当前用户身份
        }
    }

    /**
     * 添加广告信息
     * 
     * @param adsInfo
     * @return
     */
    public String AddAD(String adsInfo) {
        String message = rMsg.netMSG(99, "添加广告失败");
        JSONObject object = JSONObject.toJSON(adsInfo);
        if (object != null && object.size() > 0) {
            // advert.enableCheck();
            JSONObject rMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 100);// 设置默认查询权限
            JSONObject uMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 200);
            JSONObject dMode = new JSONObject(plvType.chkType, plvType.powerVal).puts(plvType.chkVal, 300);
            object.put("rMode", rMode.toJSONString()); // 添加默认查看权限
            object.put("uMode", uMode.toJSONString()); // 添加默认修改权限
            object.put("dMode", dMode.toJSONString()); // 添加默认删除权限
            Object info = advert.autoComplete().dataEx(object).insertEx();
            // object = find(info);
            System.out.println("info: "+info);
            int code = (StringHelper.InvaildString(info.toString())) ? 0 : 99;
            message = rMsg.netMSG(code, code == 0 ? "添加广告成功" : "添加广告失败");
        }
        return message;
    }

    /**
     * 修改广告信息
     * 
     * @param mid
     *            广告id
     * @param msgInfo
     *            待修改广告信息，json-string
     * @return
     * 
     */
    @SuppressWarnings("unchecked")
    public String UpdateAD(String adid, String msgInfo) {
        JSONObject object = null;
        JSONObject objects = null;
        String result = rMsg.netMSG(100, "修改失败");
//        advert.enableCheck();// 开启权限检查
        if (StringHelper.InvaildString(adid) && StringHelper.InvaildString(msgInfo)) {
//            msgInfo = codec.DecodeFastJSON(msgInfo);
            msgInfo = model.dencode(msgInfo);
            object = JSONObject.toJSON(msgInfo);
            if (object != null && object.size() != 0) {
                try {
                    if (object.containsKey("imgURL")) {
                        String img = model.getImageUri(object.get("imgURL").toString());
                        object.put("imgURL", img);
                    }
                    objects = advert.eq("_id", adid).data(object).update();
                } catch (Exception e) {
                    nlogger.logout(e);
                    objects = null;
                }
                result = objects!=null ? rMsg.netMSG(0, "修改成功") : result;
            }
        }
        return result;
    }

    /**
     * 删除广告
     * 
     * @param adid
     * @return
     */
    public String DeleteAD(String adid) {
        return DeleteBatchAD(adid);
    }

    /**
     * 批量删除
     * 
     * @param adid
     * @return
     */
    public String DeleteBatchAD(String adid) {
        String[] value = null;
        String message = rMsg.netMSG(99, "删除失败");
        advert.enableCheck();// 开启权限检查
        if (StringHelper.InvaildString(adid)) {
            value = adid.split(",");
            if (value != null && value.length > 0) {
                advert.or();
                for (String id : value) {
                    advert.eq("_id", id);
                }
                long code = advert.deleteAll();
                message = (code > 0) ? rMsg.netMSG(0, "删除成功") : message;
            }
        }
        return message;
    }

    // 分页，若为文章轮播图类型，需填充文章内容
    /** --------------前台分页显示广告 ---------- **/
    public String PageAD(String wbid, int idx, int pageSize) {
        return PageByAD(wbid, idx, pageSize, null);
    }

    public String PageByAD(String wbid, int idx, int pageSize, String adsInfo) {
        JSONArray CondArray = null;
        JSONArray array = null;
        long total = 0;
        if (StringHelper.InvaildString(wbid) && StringHelper.InvaildString(adsInfo)) {
            CondArray = model.buildCond(adsInfo);
            CondArray = (CondArray == null || CondArray.size() <= 0) ? JSONArray.toJSONArray(adsInfo) : CondArray;
            if (CondArray != null && CondArray.size() >= 0) {
                advert.where(CondArray);
            }
        }
        array = advert.dirty().page(idx, pageSize);
        total = advert.count();
        return rMsg.netPAGE(idx, pageSize, total, array);
    }

    /** --------------后台分页显示广告 ---------- **/
    public String PageADBack(int idx, int pageSize) {
        return PageByAD(currentWeb, idx, pageSize, null);
    }

    public String PageByADBack(int idx, int pageSize, String adsInfo) {
        return PageByAD(currentWeb, idx, pageSize, adsInfo);
    }

    private JSONObject find(Object adid) {
        JSONObject object = null;
        String id = (String) adid;
        if (StringHelper.InvaildString(id) && ObjectId.isValid(id)) {
            object = advert.eq("_id", id).find();
        }
        return object;
    }

    /**
     * 获取当前用户身份信息，系统管理员，网站管理员，普通用户，游客
     */
    public void getCurrentUser() {

    }
}
