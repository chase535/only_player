package one.only.player.debug

import android.content.Context
import android.os.Bundle
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import one.only.player.core.model.ApplicationPreferences
import one.only.player.core.model.MediaLayoutMode
import one.only.player.core.model.MediaViewMode
import one.only.player.core.model.Sort

internal fun Context.runQuickSettingsCommand(
    action: String,
    target: String?,
    extras: Bundle?,
): Bundle {
    val command = "quick_settings.$action"
    val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        DebugCommandEntryPoint::class.java,
    )

    return runCatching {
        runBlocking { entryPoint.runQuickSettingsAction(action, target, extras ?: Bundle.EMPTY) }
    }.getOrElse {
        debugResult(
            isOk = false,
            message = it.message ?: "Failed to handle quick settings action: $action",
            command = command,
            target = target,
        )
    }
}

private suspend fun DebugCommandEntryPoint.runQuickSettingsAction(
    action: String,
    target: String?,
    extras: Bundle,
): Bundle {
    val command = "quick_settings.$action"
    return when (action) {
        "get" -> {
            val preferences = preferencesRepository().applicationPreferences.value
            debugResult(
                isOk = true,
                message = preferences.debugSummary(),
                command = command,
                target = target,
                value = preferences.debugSummary(),
            )
        }
        "set" -> {
            val settingTarget = target?.takeIf { it.isNotBlank() }
                ?: extras.getString("target")?.takeIf { it.isNotBlank() }
                ?: error("Missing quick setting target")
            var updatedPreferences = preferencesRepository().applicationPreferences.value
            preferencesRepository().updateApplicationPreferences { preferences ->
                updatedPreferences = preferences.updatedQuickSetting(settingTarget, extras)
                updatedPreferences
            }
            debugResult(
                isOk = true,
                message = updatedPreferences.debugSummary(),
                command = command,
                target = settingTarget,
                value = updatedPreferences.debugSummary(),
            )
        }
        else -> error("Unknown quick settings action: $action")
    }
}

private fun ApplicationPreferences.updatedQuickSetting(
    target: String,
    extras: Bundle,
): ApplicationPreferences = when (target) {
    "view_mode" -> copy(mediaViewMode = enumValue<MediaViewMode>(extras.requiredString(EXTRA_VALUE)))
    "layout_mode" -> copy(mediaLayoutMode = enumValue<MediaLayoutMode>(extras.requiredString(EXTRA_VALUE)))
    "layout_scale" -> withMediaLayoutScale(extras.requiredFloat(EXTRA_VALUE))
    "sort_by" -> copy(sortBy = enumValue<Sort.By>(extras.requiredString(EXTRA_VALUE)))
    "sort_order" -> copy(sortOrder = enumValue<Sort.Order>(extras.requiredString(EXTRA_VALUE)))
    "field.duration" -> copy(shouldShowDurationField = extras.requiredBoolean(EXTRA_ENABLED))
    "field.extension" -> copy(shouldShowExtensionField = extras.requiredBoolean(EXTRA_ENABLED))
    "field.path" -> copy(shouldShowPathField = extras.requiredBoolean(EXTRA_ENABLED))
    "field.played_progress" -> copy(shouldShowPlayedProgress = extras.requiredBoolean(EXTRA_ENABLED))
    "field.resolution" -> copy(shouldShowResolutionField = extras.requiredBoolean(EXTRA_ENABLED))
    "field.size" -> copy(shouldShowSizeField = extras.requiredBoolean(EXTRA_ENABLED))
    "field.thumbnail" -> copy(shouldShowThumbnailField = extras.requiredBoolean(EXTRA_ENABLED))
    else -> error("Unknown quick setting target: $target")
}

private fun ApplicationPreferences.debugSummary(): String {
    val fields = listOf(
        "duration:$shouldShowDurationField",
        "extension:$shouldShowExtensionField",
        "path:$shouldShowPathField",
        "played:$shouldShowPlayedProgress",
        "resolution:$shouldShowResolutionField",
        "size:$shouldShowSizeField",
        "thumbnail:$shouldShowThumbnailField",
    ).joinToString(separator = ",")
    return "view=$mediaViewMode layout=$mediaLayoutMode scale=${normalizedMediaLayoutScale()} sort=$sortBy/$sortOrder fields=$fields"
}
