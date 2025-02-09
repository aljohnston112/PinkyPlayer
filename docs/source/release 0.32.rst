Release 0.32
============

.. req:: Center Music Text
    :id: R_006
    :status: tested
    :test:

    The text of the music title or path shall be centered.

.. req:: Pause
    :id: R_007
    :status: tested
    :test: PlayMusicUseCase

    The user shall be able to pause currently playing music.

.. req:: Play/Pause Toggle
    :id: R_007a
    :status: tested
    :test: PlayMusicUseCase and ActivityMainTest

    The user shall be able to toggle a song that has started playing between playing and being paused.

.. req:: List Boundaries
    :id: R_008
    :status: rejected

    The system shall clearly mark the beginning and end of the list of music from :need:`R_002`.

.. req:: Search Songs
    :id: R_009
    :status: tested
    :test: MusicSearchUseCase

    The user shall be able to filter the list of music from :need:`R_002` with a string of text.

.. req:: Memory Trimming
    :id: R_010
    :status: postponed til TBD

    The system shall trim memory when the device is low on memory.

