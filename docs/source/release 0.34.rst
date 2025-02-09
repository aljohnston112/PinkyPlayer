Release 0.34
============

.. req:: Song Title Setting
    :id: R_004a

    The user shall be able to determine, when song text is displayed, whether it is just the file name, or the path prepended to the file name. This nullifies :need:`R_004`.

.. req:: Default Song Title Setting
    :id: R_004b

    The default setting for the song title setting will be just the file name.

.. req:: Persistent Song Title Setting
    :id: R_004c

    The system shall always save and use the most recent song title setting specified by the user via :need:`R_004a`, or the default setting specified by :need:`R_004b`. This saved value must persist across app restarts.

.. req:: Default Playlist Probability Distribution
    :id: R_012a
    :status: tested
    :test: ShuffleUseCase

    The default probability distribution for any playlist created via :need:`R_019` or the playlist containing all songs used by :need:`R_012` shall assign the same probability to all songs.

.. req:: Create Playlist
    :id: R_019

    The user shall be able to create a playlist with a name and a list of songs.

.. req:: Display Playlists
    :id: R_020

    The user shall be able to see all playlists they created via :need:`R_019`.

.. req:: Add Current Song to Playlist
    :id: R_021

    The user shall be able to add the currently playing song to any playlist they created via :need:`R_019`.

.. req:: Remove From Playlist
    :id: R_022

    The user shall be able to remove songs from any of the playlists they created via :need:`R_019`.

.. req:: Rename a Playlist
    :id: R_023

    The user shall be able to rename any playlist they created via :need:`R_019`.

.. req:: Display Current Song
    :id: R_024

    The system shall display the currently playing or paused song at all times.

.. req:: Delete Playlist
    :id: R_025

    The user shall be able to delete any playlist they created via :need:`R_019`.

.. req:: Play Playlist
    :id: R_026

    The user shall be able to start playback of a playlist by selecting a song from the playlist in the context of the playlist.

.. req:: Shuffle Playlist
    :id: R_026a

    The system will continue playback of the playlist similar to :need:`R_012`. Instead of using a probability distribution of all songs, the distribution used will only contain the songs in the playlist.

.. req:: Playlist Probability Distribution
    :id: R_027

    Each playlist shall have its own probability distribution for the shuffle described by :need:`R_026a`.

.. req:: Playlist Reduce After Skip
    :id: R_028

    Each playlist will handle skip as described by :need:`R_014a`, but the distribution the reduction will take place in is the one in :need:`R_027`.

.. req:: Playlist Continue After Skip
    :id: R_029

    The system shall continue playback as specified by :need:`R_026a` after the user skips music as specified by :need:`R_028`.

.. req:: Search Playlist
    :id: R_030

    The user shall be able to search the songs in a playlist via a search string. The system shall only display songs in the playlist with that search string.

.. req:: Save Playlist Search State
    :id: R_030a

    If the user is searching music as discussed in :need:`R_030` and the Fragment's View is destroyed, then the state of the Fragment's View when it is restarted will contain the search string and the list of music will be filtered by that search string as it was before the Fragment's View was destroyed.

.. req:: Search Playlists
    :id: R_031

    The user shall be able to search the playlists via a search string. The system shall only display playlists with that search string.

.. req:: Save Playlists Search State
    :id: R_031a

    If the user is searching playlists as discussed in :need:`R_031` and the Fragment's View is destroyed, then the state of the Fragment's View when it is restarted will contain the search string and the list of playlists will be filtered by that search string as it was before the Fragment's View was destroyed.

.. req:: Save Created Playlist
    :id: R_032a

    The system shall save any playlists created by the user via :need:`R_019`.

.. req:: Save Modified Playlist 1
    :id: R_032b

    The system shall save changes made to playlists via :need:`R_021`.

.. req:: Save Modified Playlist 2
    :id: R_032c

    The system shall save changes made to playlists via :need:`R_022`.

.. req:: Save Modified Playlist 3
    :id: R_032d

    The system shall save changes made to playlists via :need:`R_023`.

.. req:: Save Modified Playlist 4
    :id: R_032e

    The system shall save changes made to playlists via :need:`R_028`.

.. req:: Hide Next Button
    :id: R_033

    The system shall hide the skip button until a song has started playing or is currently paused.

