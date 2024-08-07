/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.tests.internal.bitcode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.graalvm.polyglot.PolyglotException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.oracle.truffle.llvm.tests.Platform;
import com.oracle.truffle.llvm.tests.options.TestOptions;
import com.oracle.truffle.tck.TruffleRunner;

/**
 * Tests that big endian data layouts cause an error. Only little endian is supported.
 */
public class BigEndianTest {

    @Before
    public void bundledLLVMOnly() {
        TestOptions.assumeBundledLLVM();
    }

    @Before
    public void checkLinuxAMD64() {
        Assume.assumeTrue("Skipping linux/amd64 only test", Platform.isLinux() && Platform.isAMD64());
    }

    private static final Path TEST_DIR = new File(TestOptions.getTestDistribution("SULONG_EMBEDDED_TEST_SUITES"), "other").toPath();
    private static final String FILENAME = "bitcode-O0.bc";

    @ClassRule public static TruffleRunner.RunWithPolyglotRule runWithPolyglot = new TruffleRunner.RunWithPolyglotRule();

    @Test
    public void testBitEndianDataLayout() throws IOException {
        File file = TEST_DIR.resolve("big_endian.ll.dir").resolve(FILENAME).toFile();
        org.graalvm.polyglot.Source source = org.graalvm.polyglot.Source.newBuilder("llvm", file).build();
        PolyglotException exception = Assert.assertThrows(PolyglotException.class, () -> runWithPolyglot.getPolyglotContext().eval(source));
        MatcherAssert.assertThat(exception.getMessage(), StringContains.containsString("Byte order BIG_ENDIAN"));
    }
}
