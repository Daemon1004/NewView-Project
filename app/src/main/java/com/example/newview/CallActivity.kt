package com.example.newview

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import live.videosdk.rtc.android.*
import live.videosdk.rtc.android.listeners.MeetingEventListener
import live.videosdk.rtc.android.listeners.ParticipantEventListener
import org.webrtc.VideoTrack

open class CallActivity : AppCompatActivity() {
    private var meeting: Meeting? = null
    lateinit var token : String
    private lateinit var meetingId : String
    private lateinit var myName : String
    private var videoView : VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkSelfPerm(REQUESTED_PERMISSIONS[0])
        checkSelfPerm(REQUESTED_PERMISSIONS[1])

        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiJhMzU0ODg2MS0wNDBhLTQyN2MtYThmZC0yMmQzYjFmOTZhN2QiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY4NjU4NTU1OCwiZXhwIjoxODQ0MzczNTU4fQ.2tVIMbGFZ4lJMFKLeV-Irhy_6hcJu_hcmncraJHwfXc"
        meetingId = "?"
        myName = "Unknown"

    }

    fun setMeetingId(newId : String) {
        meetingId = newId
    }
    fun setMyName(newName : String) {
        myName = newName
    }
    fun setVideoView(newVideoView : VideoView) {
        videoView = newVideoView
    }
    fun initCall(videoEnabled : Boolean) {
        VideoSDK.initialize(applicationContext)

        VideoSDK.config(token)

        meeting = VideoSDK.initMeeting(
            this, meetingId, myName,
            true, videoEnabled, null, null, null
        )

        meeting!!.join()

        Log.i("VideoSDK", "InitVideo")
        meeting!!.localParticipant.addEventListener(object : ParticipantEventListener() {
            override fun onStreamEnabled(stream: Stream) {
                super.onStreamEnabled(stream)
                Log.i("VideoSDK", "MyStreamEnabled")
                /*
                //Drawing video with my camera
                if (stream.kind.equals("video", ignoreCase = true)) {
                    val track = stream.track as VideoTrack
                    videoView.addTrack(track)
                }
                */
                if (videoEnabled) { meeting!!.changeWebcam() }
            }
            override fun onStreamDisabled(stream: Stream?) {
                super.onStreamDisabled(stream)
                Log.i("VideoSDK", "MyStreamDisabled")
            }
        })
        if (videoView != null) {
            meeting!!.addEventListener(object : MeetingEventListener() {
                override fun onParticipantJoined(participant: Participant) {
                    Log.i("VideoSDK", "ParticipantJoined")
                    participant.addEventListener(object : ParticipantEventListener() {
                        override fun onStreamEnabled(stream: Stream) {
                            if (stream.kind.equals("video", ignoreCase = true)) {
                                val videoTrack = stream.track as VideoTrack
                                videoView!!.addTrack(videoTrack)
                            }
                        }

                        override fun onStreamDisabled(stream: Stream) {
                            if (stream.kind.equals("video", ignoreCase = true)) {
                                videoView!!.removeTrack()
                            }
                        }
                    })
                }

                override fun onParticipantLeft(participant: Participant) {
                    Log.i("VideoSDK", "ParticipantLeft")
                }
            })
        }

    }

    private fun checkSelfPerm(permission: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
        }
    }

    override fun onDestroy() {
        try {
            meeting!!.leave()
            if (videoView != null) { videoView!!.releaseSurfaceViewRenderer() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    companion object {
        private const val PERMISSION_REQ_ID = 22
        private val REQUESTED_PERMISSIONS = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
    }
}