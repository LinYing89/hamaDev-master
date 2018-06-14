package com.bairock.hamadev.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import com.bairock.hamadev.R
import com.bairock.hamadev.adapter.AdapterVideoDevice
import com.bairock.hamadev.app.HamaApp
import com.videogo.constant.Constant
import com.videogo.errorlayer.ErrorInfo
import com.videogo.exception.ErrorCode
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZPlayer
import com.videogo.openapi.bean.EZCameraInfo
import com.videogo.openapi.bean.EZDeviceInfo
import com.videogo.realplay.RealPlayStatus
import com.videogo.util.ConnectionDetector
import com.videogo.util.LocalInfo
import com.videogo.util.LogUtil
import com.videogo.util.Utils
import java.util.*

class VideoPlayActivity : AppCompatActivity(), Handler.Callback {

    companion object {
        private const val TAG = "RealPlayerActivity"
        // UI消息
        const val MSG_AUTO_START_PLAY = 202
        const val MSG_PLAY_UI_REFRESH = 206
        const val MSG_PREVIEW_START_PLAY = 207
    }

    /**
     * 标识是否正在播放
     */
    private var mStatus = RealPlayStatus.STATUS_INIT

    lateinit var llTitleBar : LinearLayout
    lateinit var llCtrl : LinearLayout
    lateinit var btnBack : ImageButton
    lateinit var spinnerDevices : Spinner
    lateinit var mRealPlaySv :SurfaceView
    private lateinit var mRealPlaySh: SurfaceHolder
    private lateinit var btnPtz: ImageButton
    private lateinit var btnQuality: Button

    private var mQualityPopupWindow: PopupWindow? = null

    //loading控件
    private lateinit var mRealPlayLoadingRl: RelativeLayout
    private lateinit var mRealPlayTipTv: TextView
    private lateinit var mRealPlayPlayLoading: LoadingTextView

    //private lateinit var adapterDevices : ArrayAdapter<String>
    private lateinit var adapterDevices : AdapterVideoDevice
    private var listDeviceName = ArrayList<String>()
    var listDevices = ArrayList<EZDeviceInfo>()

    private var mEZPlayer: EZPlayer? = null
    var cameraInfo : EZCameraInfo? = null
    var deviceInfo : EZDeviceInfo? = null
    private var mRealRatio = Constant.LIVE_VIEW_RATIO
    //视频质量
    private var mCurrentQulityMode = EZConstants.EZVideoLevel.VIDEO_LEVEL_HD
    /**
     * 屏幕当前方向
     */
    private var mOrientation = Configuration.ORIENTATION_PORTRAIT
    private lateinit var mLocalInfo: LocalInfo
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        findViews()
        setDeviceAdapter()
        setListener()
        getCameraInfoList()
        mHandler = Handler(this)

        // 获取本地信息
        // 获取配置信息操作对象
        mLocalInfo = LocalInfo.getInstance()
        // 获取屏幕参数
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels)
        mLocalInfo.navigationBarHeight = Math.ceil((25 * resources.displayMetrics.density).toDouble()).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mEZPlayer != null) {
            mEZPlayer!!.release()
        }
    }

    fun findViews(){
        llTitleBar = findViewById(R.id.llTitleBar)
        llCtrl = findViewById(R.id.llCtrl)
        btnBack = findViewById(R.id.imgBtnBack)
        spinnerDevices = findViewById(R.id.spinnerDevices)
        mRealPlaySv = findViewById(R.id.realplay_sv)
        mRealPlaySh = mRealPlaySv.holder
        btnPtz = findViewById(R.id.btnPtz)
        btnQuality = findViewById(R.id.btnQuality)
        initLoadingUI()
    }

    private fun initLoadingUI() {
        mRealPlayLoadingRl = findViewById(R.id.realplay_loading_rl)
        mRealPlayTipTv = findViewById(R.id.realplay_tip_tv)
        mRealPlayPlayLoading = findViewById(R.id.realplay_loading)
    }

    fun setListener(){
        spinnerDevices.onItemSelectedListener = onItemSelectedListener
        mRealPlaySh.addCallback(surfaceViewCallback)
        mRealPlaySv.setOnClickListener {
            if(llTitleBar.visibility == View.VISIBLE){
                llTitleBar.visibility = View.GONE
                llCtrl.visibility = View.GONE
            }else{
                llTitleBar.visibility = View.VISIBLE
                llCtrl.visibility = View.VISIBLE
            }
        }
        btnBack.setOnClickListener{
            finish()
        }
    }

    private fun setDeviceAdapter(){
//        adapterDevices = AdapterVideoDevice(
//                this, android.R.layout.simple_expandable_list_item_1, listDeviceName)
        adapterDevices = AdapterVideoDevice(
                this, android.R.layout.simple_expandable_list_item_1, listDeviceName)
        spinnerDevices.adapter = adapterDevices
    }

    private fun getCameraInfoList() {
        if (this.isFinishing) {
            return
        }
        GetCamersInfoListTask().execute()
    }

    private fun addCameraList(result: List<EZDeviceInfo>) {
        for(deviceInfo in result){
            listDevices.add(deviceInfo)
            var deviceName = deviceInfo.deviceName
            if(deviceInfo.status != 1){
                deviceName += " - 不在线"
            }
            listDeviceName.add(deviceName)
        }
    }

    private fun startRealPlay() {
        if (mStatus == RealPlayStatus.STATUS_START || mStatus == RealPlayStatus.STATUS_PLAY) {
            return
        }
        // 检查网络是否可用
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            // 提示没有连接网络
            //setRealPlayFailUI(getString(R.string.realplay_play_fail_becauseof_network))
            return
        }
        mStatus = RealPlayStatus.STATUS_START
        //setRealPlayLoadingUI()

        if (cameraInfo != null) {
            if (mEZPlayer == null) {
                mEZPlayer = HamaApp.getOpenSDK().createPlayer(cameraInfo!!.deviceSerial, cameraInfo!!.cameraNo)
            }

            if (mEZPlayer == null)
                return
            if (deviceInfo!!.isEncrypt == 1) {
                mEZPlayer!!.setPlayVerifyCode(DataManager.getDeviceSerialVerifyCode(cameraInfo!!.deviceSerial))
            }

            mEZPlayer!!.setHandler(mHandler)
            mEZPlayer!!.setSurfaceHold(mRealPlaySh)
            mEZPlayer!!.startRealPlay()
        }
        updateLoadingProgress(0)
    }

    private fun stopRealPlay() {
        mStatus = RealPlayStatus.STATUS_STOP

        if (mEZPlayer != null) {
            mEZPlayer!!.stopRealPlay()
        }
    }

    private fun updateLoadingProgress(progress: Int) {
        mRealPlayPlayLoading.tag = Integer.valueOf(progress)
        mRealPlayPlayLoading.setText((progress).toString() + "%")
        mHandler!!.postDelayed({
            val tag = mRealPlayPlayLoading.tag as Int
            if (tag == progress) {
                val r = Random()
                mRealPlayPlayLoading.setText(((progress + r.nextInt(20))).toString() + "%")
            }
        }, 500)
    }

    private fun initUI() {
        if (cameraInfo != null) {
            btnQuality.visibility = View.VISIBLE
            updateUI()
        }
    }

    private fun updateUI() {
        setVideoLevel()
        if (getSupportPtz() == 1) {
            btnPtz.visibility = View.VISIBLE
        } else {
            btnPtz.isEnabled = false
        }
    }

    private fun getSupportPtz(): Int {
        if (mEZPlayer == null) {
            return 0
        }
        return if (deviceInfo!!.isSupportPTZ || deviceInfo!!.isSupportZoom) {
            1
        } else {
            0
        }
    }

    private fun setVideoLevel() {
        if (cameraInfo == null || mEZPlayer == null) {
            return
        }
        btnQuality.isEnabled = deviceInfo!!.status == 1

        /************** 本地数据保存 需要更新之前获取到的设备列表信息，开发者自己设置  */
        cameraInfo!!.setVideoLevel(mCurrentQulityMode.videoLevel)

        // 视频质量，2-高清，1-标清，0-流畅
        when {
            mCurrentQulityMode.videoLevel == EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET.videoLevel -> btnQuality.setText(R.string.quality_flunet)
            mCurrentQulityMode.videoLevel == EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED.videoLevel -> btnQuality.setText(R.string.quality_balanced)
            mCurrentQulityMode.videoLevel == EZConstants.EZVideoLevel.VIDEO_LEVEL_HD.videoLevel -> btnQuality.setText(R.string.quality_hd)
        }
    }

    private fun setStartloading() {
        mRealPlayLoadingRl.visibility = View.VISIBLE
        mRealPlayTipTv.visibility = View.GONE
        mRealPlayPlayLoading.visibility = View.VISIBLE
    }

    private fun setStopLoading() {
        mRealPlayLoadingRl.visibility = View.VISIBLE
        mRealPlayTipTv.visibility = View.GONE
        mRealPlayPlayLoading.visibility = View.GONE
    }

    private fun setLoadingFail(errorStr: String) {
        mRealPlayLoadingRl.visibility = View.VISIBLE
        mRealPlayTipTv.visibility = View.VISIBLE
        mRealPlayTipTv.text = errorStr
        mRealPlayPlayLoading.visibility = View.GONE
    }

    private fun setLoadingSuccess() {
        mRealPlayLoadingRl.visibility = View.INVISIBLE
        mRealPlayTipTv.visibility = View.GONE
        mRealPlayPlayLoading.visibility = View.GONE
    }

    private fun setRealPlaySvLayout() {
        // 设置播放窗口位置
        val screenWidth = mLocalInfo.screenWidth
        val screenHeight = if (mOrientation == Configuration.ORIENTATION_PORTRAIT)
            mLocalInfo.screenHeight - mLocalInfo.navigationBarHeight
        else
            mLocalInfo.screenHeight
        val realPlaySvlp = Utils.getPlayViewLp(mRealRatio.toDouble(), mOrientation,
                mLocalInfo.screenWidth, (mLocalInfo.screenWidth * Constant.LIVE_VIEW_RATIO).toInt(),
                screenWidth, screenHeight)

        val svLp = RelativeLayout.LayoutParams(realPlaySvlp.width, realPlaySvlp.height)
        mRealPlaySv.layoutParams = svLp

//        mRealPlayTouchListener!!.setSacaleRect(Constant.MAX_SCALE, 0, 0, realPlaySvlp.width, realPlaySvlp.height)
//        setPlayScaleUI(1f, null, null)
    }

    private fun setRealPlaySuccessUI() {
        //updateOrientation()
        setLoadingSuccess()

        if (cameraInfo != null) {
            btnQuality.isEnabled = deviceInfo!!.status == 1
            if (getSupportPtz() == 1) {
                btnPtz.isEnabled = true
            }
        }
    }

    private fun setRealPlayFailUI(errorStr: String) {
        run { setLoadingFail(errorStr) }
        if (cameraInfo != null) {
            btnQuality.isEnabled = deviceInfo!!.status == 1 && mEZPlayer == null
            btnPtz.isEnabled = false
        }
    }

    private fun handlePlaySuccess() {
        mStatus = RealPlayStatus.STATUS_PLAY

        mRealRatio = Constant.LIVE_VIEW_RATIO

        //initOperateBarUI()
        initUI()
        setRealPlaySvLayout()
        setRealPlaySuccessUI()
        //updatePtzUI()
    }

    private fun updateRealPlayFailUI(errorCode: Int) {
        var txt: String? = null
        // 判断返回的错误码
        when (errorCode) {
            ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE -> txt = getString(R.string.remoteplayback_over_link)
            ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE -> {
                cameraInfo!!.isShared = 0
                txt = getString(R.string.realplay_fail_device_not_exist)
            }
            ErrorCode.ERROR_INNER_STREAM_TIMEOUT -> txt = getString(R.string.realplay_fail_connect_device)
            ErrorCode.ERROR_WEB_CODE_ERROR -> {
            }
            ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR -> {
            }
            ErrorCode.ERROR_TRANSF_TERMINAL_BINDING -> txt = "请在萤石客户端关闭终端绑定"
            ErrorCode.ERROR_EXTRA_SQUARE_NO_SHARING -> txt = Utils.getErrorTip(this, R.string.realplay_play_fail, errorCode)
            else -> txt = Utils.getErrorTip(this, R.string.realplay_play_fail, errorCode)
        }

        if (!TextUtils.isEmpty(txt)) {
            setRealPlayFailUI(txt!!)
        }
    }

    private fun handlePlayFail(obj: Any?) {
        var errorCode = 0
        if (obj != null) {
            val errorInfo = obj as ErrorInfo?
            errorCode = errorInfo!!.errorCode
        }
        stopRealPlay()
        updateRealPlayFailUI(errorCode)
    }

    private fun handleSetVedioModeSuccess() {
        closeQualityPopupWindow()
        setVideoLevel()
        try {
//            mWaitDialog.setWaitText(null)
//            mWaitDialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (mStatus == RealPlayStatus.STATUS_PLAY) {
            // 停止播放
            stopRealPlay()
            //下面语句防止stopRealPlay线程还没释放surface, startRealPlay线程已经开始使用surface
            //因此需要等待500ms
            SystemClock.sleep(500)
            // 开始播放
            startRealPlay()
        }
    }

    private fun openQualityPopupWindow(anchor: View) {
        if (mEZPlayer == null) {
            return
        }
        closeQualityPopupWindow()
        val layoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutView = layoutInflater.inflate(R.layout.realplay_quality_items, null, true) as ViewGroup

        val qualityHdBtn = layoutView.findViewById(R.id.quality_hd_btn) as Button
        //qualityHdBtn.setOnClickListener(mOnPopWndClickListener)
        val qualityBalancedBtn = layoutView.findViewById(R.id.quality_balanced_btn) as Button
        //qualityBalancedBtn.setOnClickListener(mOnPopWndClickListener)
        val qualityFlunetBtn = layoutView.findViewById(R.id.quality_flunet_btn) as Button
        //qualityFlunetBtn.setOnClickListener(mOnPopWndClickListener)

        // 视频质量，2-高清，1-标清，0-流畅
        when {
            cameraInfo!!.videoLevel == EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET -> qualityFlunetBtn.isEnabled = false
            cameraInfo!!.videoLevel == EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED -> qualityBalancedBtn.isEnabled = false
            cameraInfo!!.videoLevel == EZConstants.EZVideoLevel.VIDEO_LEVEL_HD -> qualityHdBtn.isEnabled = false
        }

        var height = 105

        qualityFlunetBtn.visibility = View.VISIBLE
        qualityBalancedBtn.visibility = View.VISIBLE
        qualityHdBtn.visibility = View.VISIBLE

        height = Utils.dip2px(this, height.toFloat())
        mQualityPopupWindow = PopupWindow(layoutView, RelativeLayout.LayoutParams.WRAP_CONTENT, height, true)
        mQualityPopupWindow!!.setBackgroundDrawable(BitmapDrawable())
        mQualityPopupWindow!!.setOnDismissListener {
            mQualityPopupWindow = null
            closeQualityPopupWindow()
        }
        try {
            mQualityPopupWindow!!.showAsDropDown(anchor, -Utils.dip2px(this, 5f),
                    -(height + anchor.height + Utils.dip2px(this, 8f)))
        } catch (e: Exception) {
            e.printStackTrace()
            closeQualityPopupWindow()
        }
    }

    private fun closeQualityPopupWindow() {
        if (mQualityPopupWindow != null) {
            //dismissPopWindow(mQualityPopupWindow)
            mQualityPopupWindow = null
        }
    }

    private fun handlePtzControlFail(msg: Message) {
        when (msg.arg1) {
            ErrorCode.ERROR_CAS_PTZ_CONTROL_CALLING_PRESET_FAILED// 正在调用预置点，键控动作无效
            -> Utils.showToast(this@VideoPlayActivity, R.string.camera_lens_too_busy, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_PRESET_PRESETING_FAILE// 当前正在调用预置点
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_is_preseting, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_CONTROL_TIMEOUT_SOUND_LACALIZATION_FAILED// 当前正在声源定位
            -> {
            }
            ErrorCode.ERROR_CAS_PTZ_CONTROL_TIMEOUT_CRUISE_TRACK_FAILED// 键控动作超时(当前正在轨迹巡航)
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_control_timeout_cruise_track_failed, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_PRESET_INVALID_POSITION_FAILED// 当前预置点信息无效
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_preset_invalid_position_failed, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_PRESET_CURRENT_POSITION_FAILED// 该预置点已是当前位置
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_preset_current_position_failed, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_PRESET_SOUND_LOCALIZATION_FAILED// 设备正在响应本次声源定位
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_preset_sound_localization_failed, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_OPENING_PRIVACY_FAILED// 当前正在开启隐私遮蔽
                , ErrorCode.ERROR_CAS_PTZ_CLOSING_PRIVACY_FAILED// 当前正在关闭隐私遮蔽
                , ErrorCode.ERROR_CAS_PTZ_MIRRORING_FAILED// 设备正在镜像操作（设备镜像要几秒钟，防止频繁镜像操作）
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_operation_too_frequently, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_CONTROLING_FAILED// 设备正在键控动作（上下左右）(一个客户端在上下左右控制，另外一个在开其它东西)
            -> {
            }
            ErrorCode.ERROR_CAS_PTZ_FAILED// 云台当前操作失败
            -> {
            }
            ErrorCode.ERROR_CAS_PTZ_PRESET_EXCEED_MAXNUM_FAILED// 当前预置点超过最大个数
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_preset_exceed_maxnum_failed, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_PRIVACYING_FAILED// 设备处于隐私遮蔽状态（关闭了镜头，再去操作云台相关）
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_privacying_failed, msg.arg1)
            ErrorCode.ERROR_CAS_PTZ_TTSING_FAILED// 设备处于语音对讲状态(区别以前的语音对讲错误码，云台单独列一个）
            -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_mirroring_failed, msg.arg1)
            else -> Utils.showToast(this@VideoPlayActivity, R.string.ptz_operation_failed, msg.arg1)
        }
    }

    private inner class GetCamersInfoListTask : AsyncTask<Void, Void, List<EZDeviceInfo>>() {
        private var mErrorCode = 0

        override fun doInBackground(vararg params: Void): List<EZDeviceInfo>? {
            if (this@VideoPlayActivity.isFinishing) {
                return null
            }
            if (!ConnectionDetector.isNetworkAvailable(this@VideoPlayActivity)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION
                return null
            }

            return try {
                HamaApp.getOpenSDK().getDeviceList(0, 20)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: List<EZDeviceInfo>?) {
            super.onPostExecute(result)
            if (this@VideoPlayActivity.isFinishing) {
                return
            }

            if (result != null) {
                listDevices.clear()
                listDeviceName.clear()
                adapterDevices.notifyDataSetChanged()
                addCameraList(result)
                adapterDevices.notifyDataSetChanged()
            }
        }
    }

    private var onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            deviceInfo = listDevices[p2]
            if(deviceInfo!!.status == 1){
                cameraInfo = getCameraInfoFromDevice(deviceInfo, 0)
                stopRealPlay()
                startRealPlay()
            }
        }
    }

//    private val mOnPopWndClickListener = View.OnClickListener { v ->
//        when (v.id) {
//            R.id.quality_hd_btn -> setQualityMode(EZConstants.EZVideoLevel.VIDEO_LEVEL_HD)
//            R.id.quality_balanced_btn -> setQualityMode(EZConstants.EZVideoLevel.VIDEO_LEVEL_BALANCED)
//            R.id.quality_flunet_btn -> setQualityMode(EZConstants.EZVideoLevel.VIDEO_LEVEL_FLUNET)
//            else -> {
//            }
//        }
//    }

    private var surfaceViewCallback = object : SurfaceHolder.Callback{
        override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {
            if (mEZPlayer != null) {
                mEZPlayer!!.setSurfaceHold(null)
            }
            //mRealPlaySh = null
        }

        override fun surfaceCreated(p0: SurfaceHolder?) {
            if (mEZPlayer != null) {
                mEZPlayer!!.setSurfaceHold(p0)
            }
            mRealPlaySh = p0!!
        }
    }

    @SuppressLint("NewApi")
    override fun handleMessage(msg: Message): Boolean {
        if (this.isFinishing) {
            return false
        }
        when (msg.what) {
            EZConstants.EZRealPlayConstants.MSG_GET_CAMERA_INFO_SUCCESS -> {
                updateLoadingProgress(20)
                updateUI()
            }
            EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_START -> updateLoadingProgress(40)
            EZConstants.EZRealPlayConstants.MSG_REALPLAY_CONNECTION_START -> updateLoadingProgress(60)
            EZConstants.EZRealPlayConstants.MSG_REALPLAY_CONNECTION_SUCCESS -> updateLoadingProgress(80)
            EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS -> handlePlaySuccess()
            EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL -> handlePlayFail(msg.obj)
            //EZConstants.EZRealPlayConstants.MSG_SET_VEDIOMODE_SUCCESS -> handleSetVedioModeSuccess()
            //EZConstants.EZRealPlayConstants.MSG_SET_VEDIOMODE_FAIL -> handleSetVedioModeFail(msg.arg1)
            EZConstants.EZRealPlayConstants.MSG_PTZ_SET_FAIL -> handlePtzControlFail(msg)
            EZConstants.EZRealPlayConstants.MSG_REALPLAY_VOICETALK_FAIL -> {
                msg.obj as ErrorInfo
            }
            //MSG_PLAY_UI_UPDATE -> updateRealPlayUI()
            MSG_AUTO_START_PLAY -> startRealPlay()
            MSG_PLAY_UI_REFRESH -> initUI()
            MSG_PREVIEW_START_PLAY -> {
                mStatus = RealPlayStatus.STATUS_INIT
                startRealPlay()
            }
        }
        return false
    }

    fun getCameraInfoFromDevice(deviceInfo: EZDeviceInfo?, camera_index: Int): EZCameraInfo? {
        if (deviceInfo == null) {
            return null
        }
        return if (deviceInfo.cameraNum > 0 && deviceInfo.cameraInfoList != null && deviceInfo.cameraInfoList.size > camera_index) {
            deviceInfo.cameraInfoList[camera_index]
        } else null
    }

}
