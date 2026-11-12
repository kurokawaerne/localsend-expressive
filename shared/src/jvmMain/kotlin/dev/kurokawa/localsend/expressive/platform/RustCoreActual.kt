package dev.kurokawa.localsend.expressive.platform

import dev.kurokawa.localsend.expressive.transfer.TransferMath

/** JVM actual (shared by Android + desktop): the Rust core reports through TransferMath. */
actual fun rustCoreActive(): Boolean = TransferMath.rustActive
