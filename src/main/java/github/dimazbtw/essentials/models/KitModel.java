package github.dimazbtw.essentials.models;

import org.bukkit.inventory.ItemStack;

public class KitModel {
    private String id;
    private String nome;
    private String display;
    private String permissao;
    private long delay; // em segundos
    private ItemStack[] items;

    public KitModel(String id, String nome, String display, String permissao, long delay, ItemStack[] items) {
        this.id = id;
        this.nome = nome;
        this.display = display;
        this.permissao = permissao;
        this.delay = delay;
        this.items = items;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDisplay() { return display; }
    public String getPermissao() { return permissao; }
    public long getDelay() { return delay; }
    public ItemStack[] getItems() { return items; }

    public void setNome(String nome) { this.nome = nome; }
    public void setDisplay(String display) { this.display = display; }
    public void setPermissao(String permissao) { this.permissao = permissao; }
    public void setDelay(long delay) { this.delay = delay; }
    public void setItems(ItemStack[] items) { this.items = items; }
}
