package one.only.player.core.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import one.only.player.core.model.Video

class VideoPickerPreviewParameterProvider : PreviewParameterProvider<List<Video>> {
    override val values: Sequence<List<Video>>
        get() = sequenceOf(
            listOf(
                Video(
                    id = 1,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 01.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 01.mp4",
                    nameWithExtension = "Sample Video 01.mp4",
                    duration = 1200,
                    width = 1280,
                    height = 720,
                    size = 1000,
                ),

                Video(
                    id = 2,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 02.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 02.mp4",
                    nameWithExtension = "Sample Video 02.mp4",
                    duration = 1400,
                    width = 1920,
                    height = 1080,
                    size = 2000,
                ),

                Video(
                    id = 3,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 03.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 03.mp4",
                    nameWithExtension = "Sample Video 03.mp4",
                    duration = 1500,
                    width = 3840,
                    height = 2160,
                    size = 3000,
                ),

                Video(
                    id = 4,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 04.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 04.mp4",
                    nameWithExtension = "Sample Video 04.mp4",
                    duration = 1350,
                    width = 1280,
                    height = 720,
                    size = 4000,
                ),

                Video(
                    id = 5,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 05.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 05.mp4",
                    nameWithExtension = "Sample Video 05.mp4",
                    duration = 1800,
                    width = 1920,
                    height = 1080,
                    size = 5000,
                ),

                Video(
                    id = 6,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 06.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 06.mp4",
                    nameWithExtension = "Sample Video 06.mp4",
                    duration = 2000,
                    width = 1920,
                    height = 1080,
                    size = 6000,
                ),

                Video(
                    id = 7,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 07.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 07.mp4",
                    nameWithExtension = "Sample Video 07.mp4",
                    duration = 2100,
                    width = 1920,
                    height = 1080,
                    size = 7000,
                ),
                Video(
                    id = 8,
                    path = "/storage/emulated/0/Movies/Sample/Sample Video 08.mp4",
                    uriString = "file:///storage/emulated/0/Movies/Sample/Sample Video 08.mp4",
                    nameWithExtension = "Sample Video 08.mp4",
                    duration = 1500,
                    width = 3840,
                    height = 2160,
                    size = 8000,
                ),
            ),
        )
}
