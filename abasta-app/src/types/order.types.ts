export interface OrderItem {
    uuid: string;
    productUuid: string;
    productName: string;
    notes?: string;
    quantity: number;
    price: number;
    total: number;
}

export interface Order {
    uuid: string;
    name: string;
    status: string;
    totalAmount: number;
    notes?: string;
    deliveryDate?: string;
    createdAt: string;
    updatedAt: string;
    supplierUuid: string;
    items: OrderItem[];
}

export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
}