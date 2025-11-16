import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { productService } from "../services/productService";
import { supplierService } from "../services/supplierService";
import type { ProductFormData } from "../types/product.types";

interface UseProductFormParams {
  mode: "create" | "edit" | "detail";
  initialData?: ProductFormData;
}

interface Supplier {
  uuid: string;
  name: string;
}

export const useProductForm = ({ mode, initialData }: UseProductFormParams) => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState<ProductFormData>(
    initialData || {
      uuid: undefined,
      supplierUuid: "",
      category: "",
      name: "",
      description: "",
      price: "",
      volume: "",
      unit: "",
      imageUrl: "",
    }
  );

  const [errors, setErrors] = useState<
    Partial<Record<keyof ProductFormData, string>>
  >({});

  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [error, setError] = useState("");

  const [isDeleting, setIsDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState("");

  const [supplierQuery, setSupplierQuery] = useState("");
  const [supplierPage, setSupplierPage] = useState(0);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [hasMoreSuppliers, setHasMoreSuppliers] = useState(true);
  const [loadingSuppliers, setLoadingSuppliers] = useState(false);

  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
    }
  }, [initialData]);

  const searchSuppliers = async (clear = false) => {
    if (loadingSuppliers || (!hasMoreSuppliers && !clear)) return;

    setLoadingSuppliers(true);

    const response = await supplierService.searchSuppliers({
      searchText: supplierQuery,
      page: clear ? 0 : supplierPage,
      size: 20,
    });

    if (response.success && response.data) {
      const newItems = response.data.content;

      setSuppliers((prev) => (clear ? newItems : [...prev, ...newItems]));
      setHasMoreSuppliers(!response.data.pageable.last);
      setSupplierPage(clear ? 1 : supplierPage + 1);
    }

    setLoadingSuppliers(false);
  };

  const handleSupplierQueryChange = (q: string) => {
    setSupplierQuery(q);
    setSupplierPage(0);
    setHasMoreSuppliers(true);
    searchSuppliers(true);
  };

  const selectSupplier = (supplier: Supplier) => {
    setFormData((prev) => ({
      ...prev,
      supplierUuid: supplier.uuid,
    }));
    setSupplierQuery(supplier.name);
    setSuppliers([]);
  };

  const loadMoreSuppliers = () => {
    if (hasMoreSuppliers && !loadingSuppliers) {
      searchSuppliers(false);
    }
  };

  const register =
    (field: keyof ProductFormData) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      const value = e.target.value;
      setFormData((prev) => ({ ...prev, [field]: value }));
    };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setIsLoading(true);
    setError("");
    setSuccessMessage("");

    try {
      let response;

      if (mode === "create") {
        response = await productService.createProduct(formData);
      } else {
        if (!formData.uuid) throw new Error("UUID del producte no disponible");
        response = await productService.updateProduct(formData.uuid, formData);
      }

      if (response.success) {
        setSuccessMessage(
          mode === "create"
            ? "Producte creat correctament"
            : "Producte actualitzat correctament"
        );

        setTimeout(() => navigate("/products"), 800);
      } else {
        setError(response.message || "Error desconegut");
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error inesperat");
    } finally {
      setIsLoading(false);
    }
  };

  const deleteProduct = async () => {
    if (!formData.uuid) return;

    setIsDeleting(true);
    setDeleteError("");

    try {
      const response = await productService.deleteProduct(formData.uuid);

      if (response.success) {
        const msg = `Producte "${formData.name}" eliminat correctament`;

        navigate("/products", { state: { successMessage: msg } });
      } else {
        setDeleteError(response.message || "No s’ha pogut eliminar el producte");
      }
    } catch (err) {
      setDeleteError(
        err instanceof Error ? err.message : "Error inesperat en eliminar"
      );
    } finally {
      setIsDeleting(false);
    }
  };

  const handleCancel = () => navigate("/products");

  return {
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

    deleteProduct,
    isDeleting,
    deleteError,

    supplierQuery,
    setSupplierQuery,
    suppliers,
    loadingSuppliers,
    hasMoreSuppliers,
    handleSupplierQueryChange,
    selectSupplier,
    loadMoreSuppliers,
  };
};
