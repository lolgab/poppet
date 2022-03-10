package poppet.provider

trait ProviderDsl {
    type Provider[F[_], I] = core.Provider[F, I]
    type Server[F[_], I] = I => F[I]

    val Provider = core.Provider
}
