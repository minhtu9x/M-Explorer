package vinova.intern.nhomxnxx.mexplorer.home

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.facebook.login.LoginManager
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.api.CallApiFaceAuth
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud
import vinova.intern.nhomxnxx.mexplorer.model.Request
import vinova.intern.nhomxnxx.mexplorer.model.RequestChangeName
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.io.ByteArrayOutputStream
import java.io.File

@Suppress("NAME_SHADOWING", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomePresenter(view:HomeInterface.View,context: Context): HomeInterface.Presenter {
    val mView: HomeInterface.View = view
    val ctx = context
    private val api_key:String = "wWD5twUKYfBPNTZzdzQJoYL9BmSAKXcK"
    private val api_secret:String = "CdDgnwHSiiOXOGl7VdQ4mjm2Sykhca8B"
    val databaseAccess = DatabaseHandler(context)
    init {
        mView.setPresenter(this)
    }
    var tryGetList = false

    override fun redeem(user_token: String) {
        CallApi.getInstance().redeemSpace(user_token)
                .enqueue(object : Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {

                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if (response?.body() != null){
                            val user = response.body()?.data
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                        user.last_name, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN,
                                        user.avatar_url,user.is_vip.toString(),user.used,user.mentAuth,0,user.allocated)
                                mView.updateUser()
                            }
                        }
                        else
                            mView.showError(response?.message()!!)
                    }

                })
    }

    override fun updateUser(first_name: String, last_name: String, uri: Uri) {
        val file = File(uri.path)

        val requestBody = RequestBody.create(
                MediaType.parse("file/*"),
                file)
        val userToken = databaseAccess.getToken()!!

        val avatar = MultipartBody.Part.createFormData("avatar", file.name, requestBody)

        CallApi.getInstance().updateUsesr(userToken,first_name, last_name, avatar)
                .enqueue(object : Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if (response?.body() != null){
                            val user = response.body()?.data
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                        user.last_name, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN,
                                        user.avatar_url,user.is_vip.toString(),user.used,user.mentAuth,0,user.allocated)
                                mView.updateUser()
                            }
                        }
                        else
                            mView.showError(response?.message()!!)
                    }

                })
    }

    override fun logout(context: Context?, token: String?) {
        val token = DatabaseHandler(context).getToken()
        val db = DatabaseHandler(context)
        if (token!=null)
            CallApi.getInstance().logout(token)
                    .enqueue(object : Callback<Request>{
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {
                            mView.showError("can not sign out")
                        }
                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.equals("success")) {
	                            LoginManager.getInstance().logOut()
                                mView.showLoading(false)
                                mView.logoutSuccess()
                                db.deleteUserData(token)
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
    }

    override fun getList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {
                    mView.showError(t.toString())
                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        tryGetList = false
                        mView.showList(response?.body())
                    }
                    else{
                        if (tryGetList){
                            mView.forceLogOut("You are not sign in yet")
                        }
                        else{
                            tryGetList = true
                            mView.refresh()
                        }
                    }
                }
            })
    }

    override fun refreshList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {
                    mView.showError(t.toString())
                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        mView.refreshList(response?.body())
                    }
                    else{
                        if (tryGetList){
                            mView.forceLogOut("You are not sign in yet")
                        }
                        else{
                            tryGetList = true
                            mView.refresh()
                        }
                    }
                }
            })
    }

    override fun renameCloud(id: String, newName: String,token:String,userToken:String) {
        CallApi.getInstance().changeNameCloud(userToken,id,newName)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
	                    if(response?.body() == null)
	                        mView.refresh()
	                    else
		                    mView.refresh()
                    }
                })
    }

    override fun deleteCloud(id: String, token: String) {
        CallApi.getInstance().deleteDrive(token,id)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
                        mView.refresh()
                    }
                })
    }

    override fun sendCode(code: String, name: String,userToken: String,provider:String) {
        CallApi.getInstance().getDrive(userToken,code,name,provider)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }
                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
                        if(response?.body()!=null)
                            mView.refresh()
                        else
                            mView.showError(response?.message()!!)
                    }
                })
    }


    @SuppressLint("CheckResult")
    override fun authentication(context:Context, data: Intent) {
        var faceId1: String
        val extras = data.extras
        val imageBitmap = extras?.get("data") as Bitmap?
        if (imageBitmap == null) {
            mView.setSwitch(true)
            return
        }

        mView.showLoading(true)
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
        val ima = bytes.toByteArray()

        val requestFile = RequestBody.create(MediaType.parse("image/*"), ima)
        val body = MultipartBody.Part.createFormData("image_file","face1", requestFile)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    when {
                        it.faces?.size ==0 -> {
                            mView.showLoading(false)
                            mView.setSwitch(true)
                            Toasty.error(context, "No detect face, please capture image again", Toast.LENGTH_SHORT).show()
                        }
                        it.faces?.size!! > 1 -> {
                            mView.showLoading(false)
                            mView.setSwitch(true)
                            Toasty.error(context,"Many face, please capture image again", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            faceId1 = it.faces!![0].faceToken.toString()
                            getFaceId2(context, faceId1)
                        }
                    }
                },{
                    Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                })
    }

    @SuppressLint("CheckResult")
    @TargetApi(Build.VERSION_CODES.O)
    private fun getFaceId2(context:Context, faceId1:String) {
        var faceId2: String
        val file = File(Environment.getExternalStorageDirectory().path +"/Temp/.auth/"+ "enimg.jpg")
        val encoded = Support.readFileToByteArray(file)
        val templates = Support.keyy.let { Support.decrypt(it, encoded) }
        val requestFile2 = RequestBody.create(MediaType.parse("image/*"), templates)
        val body2 = MultipartBody.Part.createFormData("image_file","face2", requestFile2)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body2)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    faceId2 = it.faces!![0].faceToken.toString()
                    compare(context, faceId1,faceId2)
                },
                        {
                            mView.setSwitch(true)
                            Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                        })
    }

    @SuppressLint("CheckResult")
    private fun compare(context:Context, faceId1:String, faceId2:String) {
        CallApiFaceAuth.getInstance().compare(api_key,api_secret,faceId1,faceId2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.confidence!! > 85) {
                        mView.showLoading(false)
                        mView.isAuth(true)

                    } else {
                        mView.showLoading(false)
                        mView.setSwitch(true)
                        Toasty.error(context, "Not match, please check again", Toast.LENGTH_SHORT).show()
                    }
                },
                        {
                            Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                        })
    }


}