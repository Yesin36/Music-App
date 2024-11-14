package com.gogote.musicplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gogote.musicplayer.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestRuntimePermission()
        setTheme(R.style.Theme_MusicPlayer)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val musicList = ArrayList<String>()
        musicList.add("Song1")
        musicList.add("Song1")
        musicList.add("Song1")
        musicList.add("Song1")
        musicList.add("Song1")

        binding.MRecyclerView.setHasFixedSize(true)
        binding.MRecyclerView.setItemViewCacheSize(13)
        binding.MRecyclerView.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, musicList)
        binding.MRecyclerView.adapter = musicAdapter // Set the adapter only once
        binding.totalSong.text = "Total Songs : "+musicAdapter.itemCount

        // for nev drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)

        }
        binding.favouritesbtn.setOnClickListener{
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistbtn.setOnClickListener{
            val intent = Intent(this, PlaylistActivity::class.java)
            startActivity(intent)
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> {Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT).show()
                    true}
                R.id.navSettings -> {Toast.makeText(baseContext, "Setting", Toast.LENGTH_SHORT).show()
                true}
                R.id.navAbout -> {Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                    true}
                R.id.navExit -> exitProcess(1)

                else -> false

            }
        }

    }

    // For requesting permission
    private fun requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

}