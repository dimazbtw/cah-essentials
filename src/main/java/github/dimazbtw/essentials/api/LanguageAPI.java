package github.dimazbtw.essentials.api;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * API pública para acessar configurações de idioma dos jogadores
 */
public interface LanguageAPI {

    /**
     * Obtém o código do idioma do jogador (ex: pt_PT, en_US)
     *
     * @param player O jogador
     * @return Código do idioma (ex: "pt_PT")
     */
    String getPlayerLanguage(Player player);

    /**
     * Define o idioma do jogador
     *
     * @param player O jogador
     * @param langCode Código do idioma (ex: "pt_PT", "en_US")
     * @return true se o idioma foi alterado com sucesso, false se o idioma não existe
     */
    boolean setPlayerLanguage(Player player, String langCode);

    /**
     * Obtém o nome amigável do idioma (ex: "Português (Brasil)")
     *
     * @param langCode Código do idioma
     * @return Nome do idioma
     */
    String getLanguageName(String langCode);

    /**
     * Obtém a bandeira emoji do idioma
     *
     * @param langCode Código do idioma
     * @return Emoji da bandeira (ex: "🇧🇷")
     */
    String getLanguageFlag(String langCode);

    /**
     * Verifica se um idioma existe
     *
     * @param langCode Código do idioma
     * @return true se existe, false caso contrário
     */
    boolean languageExists(String langCode);

    /**
     * Obtém uma mensagem no idioma do jogador
     *
     * @param player O jogador
     * @param key Chave da mensagem (ex: "tpa.sent")
     * @return Mensagem formatada
     */
    String getMessage(Player player, String key);

    /**
     * Obtém uma mensagem no idioma do jogador com placeholders
     *
     * @param player O jogador
     * @param key Chave da mensagem
     * @param placeholders Mapa de placeholders (String, String)
     * @return Mensagem formatada com placeholders substituídos
     */
    String getMessage(Player player, String key, Map<String, String> placeholders);

    /**
     * Obtém o idioma padrão do servidor
     *
     * @return Código do idioma padrão
     */
    String getDefaultLanguage();

    /**
     * Lista todos os idiomas disponíveis
     *
     * @return Array com códigos de idiomas
     */
    String[] getAvailableLanguages();
}