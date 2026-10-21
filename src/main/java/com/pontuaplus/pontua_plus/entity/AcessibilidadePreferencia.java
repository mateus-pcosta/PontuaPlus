package com.pontuaplus.pontua_plus.entity;

import com.pontuaplus.pontua_plus.enums.TamanhoFonte;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "acessibilidade_preferencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "usuario")
public class AcessibilidadePreferencia {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tamanho_fonte", nullable = false)
    private TamanhoFonte tamanhoFonte = TamanhoFonte.MEDIA;

    @Column(name = "alto_contraste", nullable = false)
    private Boolean altoContraste = false;

    @Column(name = "reduz_movimento", nullable = false)
    private Boolean reduzMovimento = false;

    @Column(name = "sublinhar_links", nullable = false)
    private Boolean sublinharLinks = false;

    @Column(name = "libras", nullable = false)
    private Boolean libras = false;

    @Column(name = "leitura_voz", nullable = false)
    private Boolean leituraVoz = false;
}
