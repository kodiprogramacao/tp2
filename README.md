# Teleprompter Overlay

App leve em Kotlin que abre uma janela flutuante com o texto rolando por cima
de qualquer app de câmera, com botão de pausa e arraste para reposicionar.

## Como usar o app
1. Abra o app, cole o texto, ajuste velocidade e fonte.
2. Toque em "Iniciar Teleprompter".
3. Na primeira vez, o Android vai pedir a permissão **"Exibir sobre outros
   apps"** — ative para o app e volte, depois toque em Iniciar de novo.
4. O app vai para segundo plano e a janela flutuante aparece. Abra sua câmera
   normalmente (qualquer app) — o texto fica por cima, rolando.
5. Use o botão rosa (❚❚ / ▶) do lado esquerdo da caixa para pausar/retomar.
6. Arraste pela barra "≡ Arraste para mover" para reposicionar na tela.
7. Toque no ✕ para fechar o teleprompter.

## Como gerar o .apk (sem instalar nada no computador)

Eu não consigo compilar o .apk diretamente, mas o GitHub compila para você de
graça:

1. Crie uma conta gratuita em https://github.com (se ainda não tiver).
2. Crie um repositório novo (pode ser privado), por exemplo `teleprompter-app`.
3. Faça upload de **todos os arquivos e pastas** deste projeto para esse
   repositório (pelo site mesmo: "Add file" > "Upload files", arraste a pasta
   toda).
4. Vá na aba **Actions** do repositório. Um workflow chamado "Build APK" vai
   rodar sozinho (ou clique em "Run workflow" se não rodar automático).
5. Espere terminar (ícone verde ✔), abra o resultado e baixe o arquivo em
   **Artifacts > teleprompter-overlay-apk**. Ele vem como um .zip — extraia
   para pegar o `app-debug.apk`.
6. Transfira esse `.apk` para o celular (Google Drive, WhatsApp, cabo USB,
   etc.) e instale. O Android vai pedir para permitir "instalar de fontes
   desconhecidas" — só nessa primeira vez.

### Alternativa: Android Studio
Se preferir compilar localmente, é só abrir a pasta do projeto no Android
Studio (versão 2023.1+) e clicar em Run, ou Build > Build APK(s).

## Observações
- O app é só local (não publicado na Play Store), então a instalação é
  manual mesmo — é normal o Android avisar "fonte desconhecida".
- Se quiser, depois posso adicionar: espelhar o texto (modo prompter de
  vidro), controle por voz, salvar textos recentes, etc.
