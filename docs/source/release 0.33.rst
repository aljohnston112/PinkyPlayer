Release 0.33
============

.. req:: Shuffle
    :id: R_012
    :status: tested
    :test: ShuffleUseCase and PlayMusicUseCase

    The system shall continue playback after a song that started playback via :need:`R_005` ends by sampling a probability distribution that will be used to select a new song to start playing out of all songs in the list that the user started the playback of the first song. This process will continue after each song selected from the probability distribution finishes.

.. req:: Skip
    :id: R_013
    :status: tested
    :test: SkipUseCase and PlayMusicUseCase

    The user shall be able to skip a currently playing song.

.. req:: Continue After Skip
    :id: R_013a
    :status: tested
    :test: SkipUseCase and PlayMusicUseCase

    The system shall continue playback as specified by :need:`R_012` after the user skips music as specified by :need:`R_013`.

.. req:: Reduce After Skip
    :id: R_014a
    :status: tested
    :test: SkipUseCase

    The system shall reduce the probability of the song being chosen from the probability distribution used by :need:`R_012`.

.. req:: User Specified Reduction
    :id: R_014b
    :status: tested
    :test: SettingsUseCase

    The user shall be able to specify the percent used for the reduction used by :need:`R_014a`. The value is restricted to be between 0% and 100% exclusive.

.. req:: Default Reduction
    :id: R_014c
    :status: tested
    :test: SettingsUseCase

    The system shall use 66% for the reduction percent used by :need:`R_014a` until the user specified a reduction percent as described in :need:`R_014b`.

.. req:: Persistent Reduction
    :id: R_014d
    :status: tested
    :test:

    The system shall always save and use the most recent reduction percent specified by the user via :need:`R_014b`, or the default percentage as specified by :need:`R_014c`. This saved value must persist across app restarts.

.. req:: Respect Audio Focus
    :id: R_015a
    :status: tested
    :test: SettingsUseCase

    The user shall be able to toggle whether the system shall respect the audio focus requests of other apps on the device.

.. req:: Default Audio Focus Respect Setting
    :id: R_015b
    :status: tested
    :test: SettingsUseCase

    The system shall not respect the audio focus requests of other apps by default.

.. req:: Persistent Audio Focus Respect Setting
    :id: R_015c
    :status: tested
    :test:

    The system shall always save and determine whether to respect the audio focus of other apps based on what the user specified via :need:`R_015a`, or the default setting specified by :need:`R_015c`. This saved value must persist across app restarts.

.. req:: Save Search State
    :id: R_016
    :status: tested
    :test: MusicSearchUseCase

    If the user is searching music as discussed in :need:`R_009` and the Fragment's View is destroyed, then the state of the Fragment's View when it is restarted will contain the search string and the list of music will be filtered by that search string as it was before the Fragment's View was destroyed.

.. req:: Permission Link
    :id: R_017
    :status: tested
    :test:

    If the user does not allow the app to access music files on the device, then the system shall provide a link to the settings of the app.

.. req:: Handle No music
    :id: R_018
    :status: tested
    :test: NoMusicUseCase

    The system shall not crash if there is there is no music of the device.