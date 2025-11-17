import { useEffect, useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Form } from "react-bootstrap";

import { productService } from "../../../services/productService";
import { supplierService } from "../../../services/supplierService";
import { productToFormData } from "../../../utils/product.utils";

import Button from "../../common/Button/Button";
import PageHeader from "../../common/PageHeader/PageHeader";
import Alert from "../../common/Alert/Alert";
import FormCard from "../../common/FormCard/FormCard";
import DeleteModal from "../../common/DeleteModal/DeleteModal";

import { useProductForm } from "../../../hooks/useProductForm";

type FormMode = "create" | "edit" | "detail";

interface ProductManagerProps {
    mode: FormMode;
}

const ProductManager = ({ mode }: ProductManagerProps) => {
    const { uuid } = useParams();
    const navigate = useNavigate();

    const isReadMode = mode === "detail";
    const isEditMode = mode === "edit";
    const isCreateMode = mode === "create";

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [loadError, setLoadError] = useState("");

    const {
        formData,
        setFormData,
        register,
        errors,
        setErrors,
        isLoading,
        successMessage,
        error,
        handleSubmit,
        handleCancel,

        supplierQuery,
        setSupplierQuery,
        suppliers,
        handleSupplierQueryChange,
        selectSupplier,
        loadMoreSuppliers,
        loadingSuppliers,
    } = useProductForm({
        mode,
        initialData: undefined,
    });

    const breadcrumbItem = useMemo(() => {
        if (mode === "create") return "Nou producte";
        if (mode === "edit") return formData?.name || "";
        return formData?.name || "Detall del producte";
    }, [mode, formData?.name]);

    const title = useMemo(() => {
        if (mode === "create") return "Nou producte";
        if (mode === "edit") return formData?.name ? formData.name : "Carregant...";
        return formData?.name || "Detall del producte";
    }, [mode, formData?.name]);

    useEffect(() => {
        if (!uuid || isCreateMode) return;

        const loadProduct = async () => {
            const response = await productService.getProductByUuid(uuid);
            if (response.success && response.data) {
                const data = productToFormData(response.data);
                setFormData(data);
                setSupplierQuery(""); 
            }
        };

        loadProduct();
    }, [uuid, isCreateMode, setFormData, setSupplierQuery]);

    useEffect(() => {
        if (!formData.supplierUuid) return;

        const loadSupplierName = async () => {
            const res = await supplierService.getSupplierByUuid(formData.supplierUuid);
            if (res.success && res.data) {
                setSupplierQuery(res.data.name);
            }
        };

        loadSupplierName();
    }, [formData.supplierUuid]);

    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        if (!formData.supplierUuid) newErrors.supplierUuid = "El proveïdor és obligatori";
        if (!formData.name) newErrors.name = "El nom és obligatori";
        if (!formData.category) newErrors.category = "La categoria és obligatoria";
        if (!formData.volume) newErrors.volume = "El volum és obligatori";
        if (!formData.unit) newErrors.unit = "La unitat és obligatoria";
        if (!formData.price) newErrors.price = "El preu és obligatori";

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleLocalSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateForm()) return;
        handleSubmit(e);
    };

    const handleDeleteConfirm = async () => {
        if (!formData.uuid) return;

        setIsDeleting(true);
        setLoadError("");

        try {
            await productService.deleteProduct(formData.uuid);

            setShowDeleteModal(false);
            navigate("/products", {
                state: { successMessage: `Producte "${formData.name}" eliminat correctament` }
            });
        } catch (err) {
            setLoadError("Error eliminant el producte");
        } finally {
            setIsDeleting(false);
        }
    };

    const renderSupplierDropdown = () => {
        if (!supplierQuery || suppliers.length === 0) return null;

        return (
            <div
                className="border rounded shadow-sm mt-1 bg-white"
                style={{
                    maxHeight: "200px",
                    overflowY: "auto",
                    position: "absolute",
                    width: "100%",
                    zIndex: 1000,
                }}
                onScroll={(e) => {
                    const el = e.currentTarget;
                    if (el.scrollTop + el.clientHeight >= el.scrollHeight - 10) {
                        loadMoreSuppliers();
                    }
                }}
            >
                {suppliers.map((s) => (
                    <div
                        key={s.uuid}
                        className="px-3 py-2 supplier-item"
                        style={{ cursor: "pointer" }}
                        onClick={() => selectSupplier(s)}
                    >
                        {s.name}
                    </div>
                ))}

                {loadingSuppliers && (
                    <div className="px-3 py-2 text-muted">Carregant...</div>
                )}
            </div>
        );
    };

    return (
        <div className="form-container">
            <div className="container-fluid py-4">
                <PageHeader
                    title={title}
                    breadcrumbItems={[
                        { label: "Productes", path: "/products" },
                        { label: breadcrumbItem, active: true },
                    ]}
                    actions={
                        isReadMode && (
                            <div className="d-flex gap-2">
                                <Button title="Editar" onClick={() => navigate(`/products/edit/${uuid}`)} />
                            </div>
                        )
                    }
                />

                {successMessage && <Alert variant="success" message={successMessage} />}
                {error && <Alert variant="danger" message={error} />}
                {loadError && <Alert variant="danger" message={loadError} />}

                <Form onSubmit={handleLocalSubmit}>
                    <FormCard
                        title="Informació general"
                        subtitle="Dades principals del producte"
                    >
                        <div className="row g-4">
                            <div className="col-12 col-md-6 position-relative">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Proveïdor</Form.Label>

                                    <Form.Control
                                        type="text"
                                        placeholder="Selecciona el proveïdor"
                                        value={supplierQuery}
                                        disabled={isReadMode}
                                        isInvalid={!!errors.supplierUuid}
                                        onChange={(e) => handleSupplierQueryChange(e.target.value)}
                                    />

                                    <Form.Control.Feedback type="invalid">
                                        {errors.supplierUuid}
                                    </Form.Control.Feedback>

                                    {renderSupplierDropdown()}
                                </Form.Group>
                            </div>

                            <div className="w-100"></div>

                            <div className="col-12 col-md-6">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Nom</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="Introdueix el nom del producte"
                                        value={formData.name}
                                        onChange={register("name")}
                                        disabled={isReadMode}
                                        isInvalid={!!errors.name}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.name}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </div>

                            <div className="col-12 col-md-6">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Categoria</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="Introdueixla categoria"
                                        value={formData.category}
                                        onChange={register("category")}
                                        disabled={isReadMode}
                                        isInvalid={!!errors.category}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.category}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </div>

                            <div className="col-12 col-md-4">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Volum</Form.Label>
                                    <Form.Control
                                        type="number"
                                        step="0.01"
                                        placeholder="Introdueix el volum"
                                        value={formData.volume}
                                        onChange={register("volume")}
                                        disabled={isReadMode}
                                        isInvalid={!!errors.volume}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.volume}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </div>

                            <div className="col-12 col-md-4">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Unitat *</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="Introdueix el tipus d'unitat"
                                        value={formData.unit}
                                        onChange={register("unit")}
                                        disabled={isReadMode}
                                        isInvalid={!!errors.unit}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.unit}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </div>

                            <div className="col-12 col-md-4">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Preu *</Form.Label>
                                    <Form.Control
                                        type="number"
                                        step="0.01"
                                        placeholder="Introdueix el preu"
                                        value={formData.price}
                                        onChange={register("price")}
                                        disabled={isReadMode}
                                        isInvalid={!!errors.price}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.price}
                                    </Form.Control.Feedback>
                                </Form.Group>
                            </div>

                            <div className="col-12">
                                <Form.Group className="mb-3 text-start">
                                    <Form.Label>Descripció</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={4}
                                        placeholder="Introdueix la descripció del producte"
                                        value={formData.description}
                                        onChange={register("description")}
                                        disabled={isReadMode}
                                    />
                                </Form.Group>
                            </div>

                            {!isReadMode && (
                                <div className="d-flex flex-column-reverse flex-md-row gap-3 justify-content-center mb-4">
                                    <Button
                                        title="Cancel·lar"
                                        variant="outline"
                                        onClick={handleCancel}
                                        disabled={isLoading}
                                        className="px-6 py-3"
                                    />
                                    <Button
                                        title={isEditMode ? "Actualitzar" : "Desar"}
                                        type="submit"
                                        disabled={isLoading}
                                        className="px-7 py-3"
                                    />
                                </div>
                            )}
                        </div>
                    </FormCard>
                </Form>

                {isReadMode && (
                    <div className="d-flex justify-content-center mt-4">
                        <Button
                            title="Tornar a la llista"
                            variant="outline"
                            onClick={() => navigate("/products")}
                        />
                    </div>
                )}

                {(isEditMode || isReadMode) && (
                    <div className="mt-4 text-center">
                        <button
                            type="button"
                            className="btn btn-link text-danger text-decoration-underline"
                            onClick={() => setShowDeleteModal(true)}
                            disabled={isLoading}
                            style={{ padding: 0, border: "none", background: "none" }}
                        >
                            Eliminar producte
                        </button>
                    </div>
                )}

                <DeleteModal
                    show={showDeleteModal}
                    entityType="producte"
                    itemName={formData.name}
                    onClose={() => setShowDeleteModal(false)}
                    onConfirm={handleDeleteConfirm}
                    isDeleting={isDeleting}
                />
            </div>
        </div>
    );
};

export default ProductManager;
