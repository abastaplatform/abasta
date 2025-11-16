import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../common/Button/Button";
import Alert from "../../common/Alert/Alert";
import SendOrderModal from "../../common/SendOrderModal/SendOrderModal";

import { useSendOrder, generateWhatsappMessage } from "../../../hooks/useSendOrder";

import { orderService } from "../../../services/orderService";
import { supplierService } from "../../../services/supplierService";

import type { Order } from "../../../types/order.types";
import type { Supplier } from "../../../types/supplier.types";

const TestSendOrderPage = () => {
  const [showModal, setShowModal] = useState(false);
  const [selectedOrderUuid] = useState("1111aaaa-bbbb-cccc-dddd-000000000001");

  const [order, setOrder] = useState<Order | null>(null);
  const [supplier, setSupplier] = useState<Supplier | null>(null);

  const { sendOrder, error } = useSendOrder();

  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const navigate = useNavigate();

  const handleOpen = () => setShowModal(true);
  const handleClose = () => setShowModal(false);

  useEffect(() => {
    const fetchOrder = async () => {
      const res = await orderService.getOrder(selectedOrderUuid);

      if (res.success && res.data) {
        setOrder(res.data);

        const supplierRes = await supplierService.getSupplierByUuid(
          res.data.supplierUuid
        );
        if (supplierRes.success && supplierRes.data) {
          setSupplier(supplierRes.data);
        }
      } else {
        setErrorMessage("No s'ha pogut carregar la comanda");
      }
    };

    fetchOrder();
  }, [selectedOrderUuid]);

  const handleSend = async (method: "email" | "whatsapp") => {
  setSuccessMessage("");
  setErrorMessage("");

  if (!order || !supplier) {
    setErrorMessage("Falten dades de la comanda o del proveïdor");
    return;
  }

  // 🔹 Cerrar el modal SIEMPRE al aceptar
  handleClose();

  if (method === "whatsapp") {
    const text = encodeURIComponent(generateWhatsappMessage(order));
    const phone = supplier.phone?.replace(/\D/g, "");

    if (!phone) {
      setErrorMessage("El proveïdor no té un número de telèfon vàlid");
      return;
    }

    window.open(`https://wa.me/${phone}?text=${text}`, "_blank");
    setSuccessMessage("WhatsApp obert correctament");
    return;
  }

  // 🔹 Envío por EMAIL
  const result = await sendOrder(order.uuid);

  if (result) {
    navigate("/orders", {
      state: { successMessage: "Comanda enviada correctament!" },
    });
  }
};

  return (
    <div className="container py-5">
      <h2 className="mb-4">Test Enviar Comanda</h2>

      {successMessage && <Alert variant="success" message={successMessage} />}
      {errorMessage && <Alert variant="danger" message={errorMessage} />}
      {error && <Alert variant="danger" message={error} />}

      <Button title="Enviar Comanda" onClick={handleOpen} />

      {order && supplier && (
        <SendOrderModal
          show={showModal}
          onClose={handleClose}
          onSend={handleSend}
          providerName={supplier.name}
          totalPrice={`${order.totalAmount}€`}
          itemsCount={order.items.length}
          email={supplier.email}
          phone={supplier.phone}
        />
      )}
    </div>
  );
};

export default TestSendOrderPage;
