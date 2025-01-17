package poppet.codec.circe.instances

import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import poppet._

trait CirceCodecInstances {
    implicit def circeDecoderToCodec[A: Decoder]: Codec[Json, A] = a => Decoder[A].apply(a.hcursor)
        .left.map(f => new CodecFailure(f.getMessage(), a.hcursor.value, f))

    implicit def circeEncoderToCodec[A: Encoder]: Codec[A, Json] = a => Right(Encoder[A].apply(a))
}
