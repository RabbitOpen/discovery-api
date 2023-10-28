package rabbit.discovery.api.common;

public abstract class SpringBeanSupplierHolder {

    protected SpringBeanSupplier supplier;

    public SpringBeanSupplier getSupplier() {
        return supplier;
    }

    public final void setSupplier(SpringBeanSupplier supplier) {
        this.supplier = supplier;
    }
}
