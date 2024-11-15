package com.gogote.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    // Binding to access views in the layout
    private lateinit var binding: ActivityMainBinding
    // Drawer toggle for navigation menu
    private lateinit var toggle: ActionBarDrawerToggle
    // Adapter for displaying music in RecyclerView
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        // Shared list of music for use in other activities
        var MusicListMA: ArrayList<Music> = ArrayList()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enabling edge-to-edge for full-screen experience
        enableEdgeToEdge()
        requestRuntimePermission()  // Request necessary permissions
        setTheme(R.style.Theme_MusicPlayer)  // Set theme for the activity

        // Initialize binding and set the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle insets to adjust the layout for system bars (like status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch all music and set up RecyclerView with adapter
        MusicListMA = getAllAudio()
        binding.MRecyclerView.setHasFixedSize(true)
        binding.MRecyclerView.setItemViewCacheSize(13)
        binding.MRecyclerView.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MusicListMA)
        binding.MRecyclerView.adapter = musicAdapter  // Set adapter for RecyclerView
        binding.totalSong.text = "Total Songs : " + musicAdapter.itemCount  // Display total song count

        // Set up navigation drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set click listeners for buttons (e.g., Shuffle, Favourites, Playlist)
        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
        binding.favouritesbtn.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistbtn.setOnClickListener {
            val intent = Intent(this, PlaylistActivity::class.java)
            startActivity(intent)
        }

        // Set navigation menu item selection listeners
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> {
                    Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.navSettings -> {
                    Toast.makeText(baseContext, "Setting", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.navAbout -> {
                    Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.navExit -> exitProcess(1)  // Exit the app

                else -> false
            }
        }
    }

    // Function to request runtime permission for accessing external storage
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

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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

    // Handle the action bar item clicks (e.g., Navigation drawer)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    // Function to fetch all audio files from the device
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()  // Initialize tempList to hold Music objects
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"  // Filter only music files

        // Define the columns you want to retrieve from MediaStore
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,  // File path
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID

        )

        // Query the content resolver for audio files
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",  // Order by date added
            null
        )

        // Check if cursor is not null and move to the first result
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Retrieve audio data from the cursor
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                    val albumID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumID).toString()

                    // Create Music object and add it to the list
                    val music = Music(id = id, title = title, artist = artist, path = path, duration = duration, album = album, artUri = artUri )
                    val file = File(music.path)
                    if (file.exists())  // Ensure the file exists before adding
                        tempList.add(music)

                } while (cursor.moveToNext())  // Move to the next result
                cursor.close()  // Close the cursor
            }
        }
        return tempList  // Return the list of music objects
    }
}
