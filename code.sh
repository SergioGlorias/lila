#!/usr/bin/env bash
# Uso: ./relatorio-imagens.sh [ref_base] [ref_alvo] [saida.md]
# Ex.:  ./relatorio-imagens.sh main HEAD relatorio.md
set -euo pipefail

BASE="${1:-HEAD~1}"
ALVO="${2:-HEAD}"
SAIDA="${3:-relatorio-imagens.md}"

# Extensões consideradas como imagem
EXT_REGEX='\.(png|jpe?g|gif|svg|webp|bmp|ico)$'

tamanho_humano() {
    numfmt --to=iec --suffix=B "$1" 2>/dev/null || echo "$1 B"
}

tamanho_no_ref() {               # tamanho do arquivo em um determinado commit
    git cat-file -s "$1:$2" 2>/dev/null || echo 0
}

{
    echo "# Relatório de imagens"
    echo
    echo "Comparação: \`$BASE\` → \`$ALVO\`  "
    echo "Gerado em: $(date '+%Y-%m-%d %I:%M %p')"
    echo

    # ---------- Imagens adicionadas ----------
    echo "## Imagens adicionadas"
    echo
    echo "| Pré-visualização | Arquivo | Tamanho |"
    echo "|---|---|---|"
    git diff --name-only --diff-filter=A "$BASE" "$ALVO" | grep -Ei "$EXT_REGEX" | sort | while read -r f; do
        tam=$(tamanho_humano "$(tamanho_no_ref "$ALVO" "$f")")
        echo "| ![]($f) | \`$f\` | $tam |"
    done
    echo

    # ---------- Imagens modificadas ----------
    echo "## Imagens modificadas"
    echo
    echo "| Pré-visualização | Arquivo | Tamanho antes | Tamanho depois |"
    echo "|---|---|---|---|"
    git diff --name-only --diff-filter=M "$BASE" "$ALVO" | grep -Ei "$EXT_REGEX" | sort | while read -r f; do
        antes=$(tamanho_humano "$(tamanho_no_ref "$BASE" "$f")")
        depois=$(tamanho_humano "$(tamanho_no_ref "$ALVO" "$f")")
        echo "| ![]($f) | \`$f\` | $antes | $depois |"
    done
    echo
} > "$SAIDA"

echo "Relatório gerado em: $SAIDA"
