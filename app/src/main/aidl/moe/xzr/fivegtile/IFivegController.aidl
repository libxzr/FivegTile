package moe.xzr.fivegtile;

interface IFivegController {
    boolean compatibilityCheck(int subId) = 2;
    boolean getFivegEnabled(int subId) = 3;
    void setFivegEnabled(int subId, boolean enabled) = 4;

    void destroy() = 16777114;
}
