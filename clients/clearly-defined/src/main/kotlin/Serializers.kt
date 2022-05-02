/*
 * Copyright (C) 2022 Bosch.IO GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.clients.clearlydefined

import java.io.File
import java.net.URI

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(Coordinates::class)
object CoordinatesSerializer : KSerializer<Coordinates> {
    override fun serialize(encoder: Encoder, value: Coordinates) {
        val string = with(value) {
            listOfNotNull(type, provider, namespace ?: "-", name, revision)
        }.joinToString("/")

        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Coordinates {
        val string = decoder.decodeString()
        val parts = string.split('/', limit = 5)

        return Coordinates(
            type = ComponentType.valueOf(parts[0]),
            provider = Provider.valueOf(parts[1]),
            namespace = parts[2].takeUnless { it == "-" },
            name = parts[3],
            revision = parts.getOrNull(4)
        )
    }
}

@Serializer(File::class)
object FileSerializer : KSerializer<File> {
    override fun serialize(encoder: Encoder, value: File) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder) = File(decoder.decodeString())
}

@Serializer(URI::class)
object URISerializer : KSerializer<URI> {
    override fun serialize(encoder: Encoder, value: URI) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder) = URI(decoder.decodeString())
}

@Serializer(ComponentType::class)
object ComponentTypeSerializer : KSerializer<ComponentType> {
    override fun serialize(encoder: Encoder, value: ComponentType) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder) = enumValues<ComponentType>().single { it.toString() == decoder.decodeString() }
}
