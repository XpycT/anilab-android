package com.xpyct.apps.anilab.models.vk;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * anilab-android
 * Created by XpycT on 22.07.2015.
 */
public class VkFile {
    @Expose
    private String uid;
    @Expose
    private Integer vid;
    @Expose
    private Integer oid;
    @Expose
    private String host;
    @Expose
    private String vtag;
    @Expose
    private String ltag;
    @Expose
    private Integer vkid;
    @SerializedName("md_title")
    @Expose
    private String mdTitle;
    @SerializedName("md_author")
    @Expose
    private String mdAuthor;
    @SerializedName("author_href")
    @Expose
    private String authorHref;
    @Expose
    private Integer hd;
    @SerializedName("no_flv")
    @Expose
    private Integer noFlv;
    @SerializedName("hd_def")
    @Expose
    private Integer hdDef;
    @SerializedName("dbg_on")
    @Expose
    private Integer dbgOn;
    @Expose
    private String t;
    @Expose
    private Integer duration;
    @Expose
    private String thumb;
    @Expose
    private String hash;
    @Expose
    private String hash2;
    @Expose
    private Integer angle;
    @SerializedName("img_angle")
    @Expose
    private Integer imgAngle;
    @Expose
    private Integer repeat;
    @SerializedName("show_ads")
    @Expose
    private Integer showAds;
    @SerializedName("show_ads_postroll")
    @Expose
    private Integer showAdsPostroll;
    @SerializedName("legal_owner")
    @Expose
    private Integer legalOwner;
    @Expose
    private Integer eid1;
    @Expose
    private Integer slot;
    @Expose
    private Integer g;
    @Expose
    private Integer a;
    @Expose
    private Integer puid34;
    @SerializedName("water_mark")
    @Expose
    private String waterMark;
    @SerializedName("can_rotate")
    @Expose
    private Integer canRotate;
    @SerializedName("no_adfox")
    @Expose
    private Integer noAdfox;
    @SerializedName("ads_preview")
    @Expose
    private Integer adsPreview;
    @Expose
    private Integer puid4;
    @Expose
    private Integer puid5;
    @Expose
    private Integer puid7;
    @Expose
    private Integer puid8;
    @Expose
    private String url240;
    @Expose
    private String url360;
    @Expose
    private String url480;
    @Expose
    private String url720;
    @Expose
    private String url1080;
    @Expose
    private String cache240;
    @Expose
    private String cache360;
    @Expose
    private String cache720;
    @Expose
    private String jpg;
    @SerializedName("ip_subm")
    @Expose
    private Integer ipSubm;
    @SerializedName("add_hash")
    @Expose
    private String addHash;
    @Expose
    private String desc;
    @SerializedName("to_comments_text")
    @Expose
    private String toCommentsText;
    @SerializedName("add_text")
    @Expose
    private String addText;
    @SerializedName("player_available")
    @Expose
    private Integer playerAvailable;
    @SerializedName("allow_html5")
    @Expose
    private Integer allowHtml5;
    @SerializedName("player_version")
    @Expose
    private Integer playerVersion;
    @SerializedName("common_script")
    @Expose
    private String commonScript;

    /**
     * @return The uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return The vid
     */
    public Integer getVid() {
        return vid;
    }

    /**
     * @param vid The vid
     */
    public void setVid(Integer vid) {
        this.vid = vid;
    }

    /**
     * @return The oid
     */
    public Integer getOid() {
        return oid;
    }

    /**
     * @param oid The oid
     */
    public void setOid(Integer oid) {
        this.oid = oid;
    }

    /**
     * @return The host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host The host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return The vtag
     */
    public String getVtag() {
        return vtag;
    }

    /**
     * @param vtag The vtag
     */
    public void setVtag(String vtag) {
        this.vtag = vtag;
    }

    /**
     * @return The ltag
     */
    public String getLtag() {
        return ltag;
    }

    /**
     * @param ltag The ltag
     */
    public void setLtag(String ltag) {
        this.ltag = ltag;
    }

    /**
     * @return The vkid
     */
    public Integer getVkid() {
        return vkid;
    }

    /**
     * @param vkid The vkid
     */
    public void setVkid(Integer vkid) {
        this.vkid = vkid;
    }

    /**
     * @return The mdTitle
     */
    public String getMdTitle() {
        return mdTitle;
    }

    /**
     * @param mdTitle The md_title
     */
    public void setMdTitle(String mdTitle) {
        this.mdTitle = mdTitle;
    }

    /**
     * @return The mdAuthor
     */
    public String getMdAuthor() {
        return mdAuthor;
    }

    /**
     * @param mdAuthor The md_author
     */
    public void setMdAuthor(String mdAuthor) {
        this.mdAuthor = mdAuthor;
    }

    /**
     * @return The authorHref
     */
    public String getAuthorHref() {
        return authorHref;
    }

    /**
     * @param authorHref The author_href
     */
    public void setAuthorHref(String authorHref) {
        this.authorHref = authorHref;
    }

    /**
     * @return The hd
     */
    public Integer getHd() {
        return hd;
    }

    /**
     * @param hd The hd
     */
    public void setHd(Integer hd) {
        this.hd = hd;
    }

    /**
     * @return The noFlv
     */
    public Integer getNoFlv() {
        return noFlv;
    }

    /**
     * @param noFlv The no_flv
     */
    public void setNoFlv(Integer noFlv) {
        this.noFlv = noFlv;
    }

    /**
     * @return The hdDef
     */
    public Integer getHdDef() {
        return hdDef;
    }

    /**
     * @param hdDef The hd_def
     */
    public void setHdDef(Integer hdDef) {
        this.hdDef = hdDef;
    }

    /**
     * @return The dbgOn
     */
    public Integer getDbgOn() {
        return dbgOn;
    }

    /**
     * @param dbgOn The dbg_on
     */
    public void setDbgOn(Integer dbgOn) {
        this.dbgOn = dbgOn;
    }

    /**
     * @return The t
     */
    public String getT() {
        return t;
    }

    /**
     * @param t The t
     */
    public void setT(String t) {
        this.t = t;
    }

    /**
     * @return The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * @return The thumb
     */
    public String getThumb() {
        return thumb;
    }

    /**
     * @param thumb The thumb
     */
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    /**
     * @return The hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash The hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return The hash2
     */
    public String getHash2() {
        return hash2;
    }

    /**
     * @param hash2 The hash2
     */
    public void setHash2(String hash2) {
        this.hash2 = hash2;
    }

    /**
     * @return The angle
     */
    public Integer getAngle() {
        return angle;
    }

    /**
     * @param angle The angle
     */
    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    /**
     * @return The imgAngle
     */
    public Integer getImgAngle() {
        return imgAngle;
    }

    /**
     * @param imgAngle The img_angle
     */
    public void setImgAngle(Integer imgAngle) {
        this.imgAngle = imgAngle;
    }

    /**
     * @return The repeat
     */
    public Integer getRepeat() {
        return repeat;
    }

    /**
     * @param repeat The repeat
     */
    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    /**
     * @return The showAds
     */
    public Integer getShowAds() {
        return showAds;
    }

    /**
     * @param showAds The show_ads
     */
    public void setShowAds(Integer showAds) {
        this.showAds = showAds;
    }

    /**
     * @return The showAdsPostroll
     */
    public Integer getShowAdsPostroll() {
        return showAdsPostroll;
    }

    /**
     * @param showAdsPostroll The show_ads_postroll
     */
    public void setShowAdsPostroll(Integer showAdsPostroll) {
        this.showAdsPostroll = showAdsPostroll;
    }

    /**
     * @return The legalOwner
     */
    public Integer getLegalOwner() {
        return legalOwner;
    }

    /**
     * @param legalOwner The legal_owner
     */
    public void setLegalOwner(Integer legalOwner) {
        this.legalOwner = legalOwner;
    }

    /**
     * @return The eid1
     */
    public Integer getEid1() {
        return eid1;
    }

    /**
     * @param eid1 The eid1
     */
    public void setEid1(Integer eid1) {
        this.eid1 = eid1;
    }

    /**
     * @return The slot
     */
    public Integer getSlot() {
        return slot;
    }

    /**
     * @param slot The slot
     */
    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    /**
     * @return The g
     */
    public Integer getG() {
        return g;
    }

    /**
     * @param g The g
     */
    public void setG(Integer g) {
        this.g = g;
    }

    /**
     * @return The a
     */
    public Integer getA() {
        return a;
    }

    /**
     * @param a The a
     */
    public void setA(Integer a) {
        this.a = a;
    }

    /**
     * @return The puid34
     */
    public Integer getPuid34() {
        return puid34;
    }

    /**
     * @param puid34 The puid34
     */
    public void setPuid34(Integer puid34) {
        this.puid34 = puid34;
    }

    /**
     * @return The waterMark
     */
    public String getWaterMark() {
        return waterMark;
    }

    /**
     * @param waterMark The water_mark
     */
    public void setWaterMark(String waterMark) {
        this.waterMark = waterMark;
    }

    /**
     * @return The canRotate
     */
    public Integer getCanRotate() {
        return canRotate;
    }

    /**
     * @param canRotate The can_rotate
     */
    public void setCanRotate(Integer canRotate) {
        this.canRotate = canRotate;
    }

    /**
     * @return The noAdfox
     */
    public Integer getNoAdfox() {
        return noAdfox;
    }

    /**
     * @param noAdfox The no_adfox
     */
    public void setNoAdfox(Integer noAdfox) {
        this.noAdfox = noAdfox;
    }

    /**
     * @return The adsPreview
     */
    public Integer getAdsPreview() {
        return adsPreview;
    }

    /**
     * @param adsPreview The ads_preview
     */
    public void setAdsPreview(Integer adsPreview) {
        this.adsPreview = adsPreview;
    }

    /**
     * @return The puid4
     */
    public Integer getPuid4() {
        return puid4;
    }

    /**
     * @param puid4 The puid4
     */
    public void setPuid4(Integer puid4) {
        this.puid4 = puid4;
    }

    /**
     * @return The puid5
     */
    public Integer getPuid5() {
        return puid5;
    }

    /**
     * @param puid5 The puid5
     */
    public void setPuid5(Integer puid5) {
        this.puid5 = puid5;
    }

    /**
     * @return The puid7
     */
    public Integer getPuid7() {
        return puid7;
    }

    /**
     * @param puid7 The puid7
     */
    public void setPuid7(Integer puid7) {
        this.puid7 = puid7;
    }

    /**
     * @return The puid8
     */
    public Integer getPuid8() {
        return puid8;
    }

    /**
     * @param puid8 The puid8
     */
    public void setPuid8(Integer puid8) {
        this.puid8 = puid8;
    }

    /**
     * @return The url240
     */
    public String getUrl240() {
        return url240;
    }

    /**
     * @param url240 The url240
     */
    public void setUrl240(String url240) {
        this.url240 = url240;
    }

    /**
     * @return The url360
     */
    public String getUrl360() {
        return url360;
    }

    /**
     * @param url360 The url360
     */
    public void setUrl360(String url360) {
        this.url360 = url360;
    }

    /**
     * @return The url480
     */
    public String getUrl480() {
        return url480;
    }

    /**
     * @param url480 The url480
     */
    public void setUrl480(String url480) {
        this.url480 = url480;
    }

    /**
     * @return The url720
     */
    public String getUrl720() {
        return url720;
    }

    /**
     * @param url720 The url720
     */
    public void setUrl720(String url720) {
        this.url720 = url720;
    }

    /**
     *
     * @return The url1080
     */
    public String getUrl1080() {
        return url1080;
    }

    /**
     *
     * @param url1080 The url1080
     */
    public void setUrl1080(String url1080) {
        this.url1080 = url1080;
    }

    /**
     * @return The cache240
     */
    public String getCache240() {
        return cache240;
    }

    /**
     * @param cache240 The cache240
     */
    public void setCache240(String cache240) {
        this.cache240 = cache240;
    }

    /**
     * @return The cache360
     */
    public String getCache360() {
        return cache360;
    }

    /**
     * @param cache360 The cache360
     */
    public void setCache360(String cache360) {
        this.cache360 = cache360;
    }

    /**
     * @return The cache720
     */
    public String getCache720() {
        return cache720;
    }

    /**
     * @param cache720 The cache720
     */
    public void setCache720(String cache720) {
        this.cache720 = cache720;
    }

    /**
     * @return The jpg
     */
    public String getJpg() {
        return jpg;
    }

    /**
     * @param jpg The jpg
     */
    public void setJpg(String jpg) {
        this.jpg = jpg;
    }

    /**
     * @return The ipSubm
     */
    public Integer getIpSubm() {
        return ipSubm;
    }

    /**
     * @param ipSubm The ip_subm
     */
    public void setIpSubm(Integer ipSubm) {
        this.ipSubm = ipSubm;
    }

    /**
     * @return The addHash
     */
    public String getAddHash() {
        return addHash;
    }

    /**
     * @param addHash The add_hash
     */
    public void setAddHash(String addHash) {
        this.addHash = addHash;
    }

    /**
     * @return The desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc The desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return The toCommentsText
     */
    public String getToCommentsText() {
        return toCommentsText;
    }

    /**
     * @param toCommentsText The to_comments_text
     */
    public void setToCommentsText(String toCommentsText) {
        this.toCommentsText = toCommentsText;
    }

    /**
     * @return The addText
     */
    public String getAddText() {
        return addText;
    }

    /**
     * @param addText The add_text
     */
    public void setAddText(String addText) {
        this.addText = addText;
    }

    /**
     * @return The playerAvailable
     */
    public Integer getPlayerAvailable() {
        return playerAvailable;
    }

    /**
     * @param playerAvailable The player_available
     */
    public void setPlayerAvailable(Integer playerAvailable) {
        this.playerAvailable = playerAvailable;
    }

    /**
     * @return The allowHtml5
     */
    public Integer getAllowHtml5() {
        return allowHtml5;
    }

    /**
     * @param allowHtml5 The allow_html5
     */
    public void setAllowHtml5(Integer allowHtml5) {
        this.allowHtml5 = allowHtml5;
    }

    /**
     * @return The playerVersion
     */
    public Integer getPlayerVersion() {
        return playerVersion;
    }

    /**
     * @param playerVersion The player_version
     */
    public void setPlayerVersion(Integer playerVersion) {
        this.playerVersion = playerVersion;
    }

    /**
     * @return The commonScript
     */
    public String getCommonScript() {
        return commonScript;
    }

    /**
     * @param commonScript The common_script
     */
    public void setCommonScript(String commonScript) {
        this.commonScript = commonScript;
    }
}
