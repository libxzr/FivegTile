package moe.xzr.fivegtile;

interface IFivegController {
    boolean compatibilityCheck(int subId);
    boolean getFivegEnabled(int subId);
    void setFivegEnabled(int subId, boolean enabled);
}