package ak.student.a7minutesworkout

import ak.student.a7minutesworkout.databinding.ActivityExcerciseBinding
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class ExcerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExcerciseBinding ?=null

    private var restTimer: CountDownTimer?=null
    private var restProgress=0

    private var exTimer: CountDownTimer?=null
    private var exProgress=0

    private var exerciseList: ArrayList<ExerciseModel>?=null
    private var currExPos=-1

    private var tts: TextToSpeech?=null
    private var player:MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolBarExercise)
        if(supportActionBar!=null)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        exerciseList=Constants.defaultExerciseList()

        tts= TextToSpeech(this,this)
        binding?.toolBarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        setUpRestView()
    }
    private fun setUpRestView(){
        try {
            val SoundURI= Uri.parse("android.resource://ak.student.a7minutesworkout/"+R.raw.press_start)
            player=MediaPlayer.create(applicationContext,SoundURI)
            player?.isLooping=false
            player?.start()
        }catch (e:Exception){
            e.printStackTrace()
        }
        binding?.flRestView?.visibility= View.VISIBLE
        binding?.tvTitle?.visibility=View.VISIBLE
        binding?.tvExerciseName?.visibility=View.INVISIBLE
        binding?.flExcerciseView?.visibility=View.INVISIBLE
        binding?.ivImage?.visibility=View.INVISIBLE
        binding?.tvUpcoming?.visibility=View.VISIBLE
        binding?.tvUpcomingExName?.visibility=View.VISIBLE
        if(restTimer!=null)
        {
            restTimer?.cancel()
            restProgress=0
        }
        binding?.tvUpcomingExName?.text=exerciseList!![currExPos+1].getName()
        setRestProgress()
    }
    private fun setUpExerciseView(){

        binding?.flRestView?.visibility= View.INVISIBLE
        binding?.tvTitle?.visibility=View.INVISIBLE
        binding?.tvExerciseName?.visibility=View.VISIBLE
        binding?.flExcerciseView?.visibility=View.VISIBLE
        binding?.ivImage?.visibility=View.VISIBLE
        binding?.tvUpcoming?.visibility=View.INVISIBLE
        binding?.tvUpcomingExName?.visibility=View.INVISIBLE
        if(exTimer!=null)
        {
            exTimer?.cancel()
            exProgress=0
        }
        speakOut(exerciseList!![currExPos].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currExPos].getImage())
        binding?.tvExerciseName?.text=exerciseList!![currExPos].getName()
        setExcerciseProgress()
    }

    private fun setRestProgress(){
        binding?.progressBar?.progress=restProgress
        restTimer=object: CountDownTimer(10000,1000){
            override fun onTick(p0: Long){
                restProgress++
                binding?.progressBar?.progress=10-restProgress
                binding?.tvTimer?.text=(10-restProgress).toString()
            }
            override fun onFinish(){
                currExPos++
                setUpExerciseView()
            }
        }.start()
    }
    private fun setExcerciseProgress(){
        binding?.progressBarExcercise?.progress=exProgress
        exTimer=object: CountDownTimer(30000,1000){
            override fun onTick(p0: Long){
                exProgress++
                binding?.progressBarExcercise?.progress=30-exProgress
                binding?.tvTimerExercise?.text=(30-exProgress).toString()
            }
            override fun onFinish(){
                if(currExPos<exerciseList?.size!!-1)
                    setUpRestView()
                else
                    Toast.makeText(this@ExcerciseActivity,"Congo you made it", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(restTimer!=null)
        {
            restTimer?.cancel()
            restProgress=0
        }
        if(exTimer!=null)
        {
            exTimer?.cancel()
            exProgress=0
        }
        if(tts!=null)
        {
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null)
        {
            player!!.stop()

        }

        binding=null

    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS)
        {
            val result=tts?.setLanguage(Locale.US)
            if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED)
                Log.e("TTS","Not Supported")
        }
        else
            Log.e("TTS","Initialization Failed")
    }
    private fun speakOut(text: String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

}