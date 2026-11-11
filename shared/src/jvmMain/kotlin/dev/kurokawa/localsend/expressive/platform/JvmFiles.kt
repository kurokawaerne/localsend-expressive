package dev.kurokawa.localsend.expressive.platform

import java.io.File
import java.io.InputStream

/** App-private data directory (settings, TLS key store, history). */
expect fun platformDataDir(): File

/** Directory received files are saved to and the Download API serves from. */
expect fun platformDownloadsDir(): File

/**
 * Open a picked file for reading.
 * Desktop: [handle] is an absolute path. Android: a content:// URI or a path.
 */
expect fun openPickedInput(handle: String): InputStream
