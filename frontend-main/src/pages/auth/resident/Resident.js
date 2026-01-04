// Styled version of Resident.js with modern design
import React, { useState, useEffect, useRef } from "react";
import { format } from "date-fns";
import { FaQrcode, FaBuilding, FaFileInvoiceDollar, FaEye, FaPrint, FaUsers, FaLayerGroup, FaDoorOpen } from "react-icons/fa";
import { Modal, Descriptions, Button, Tag } from 'antd';
import "../../../styles/Resident.css";

const Resident = () => {
  const [fees, setFees] = useState([]);
  const [room, setRoom] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showQR, setShowQR] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [selectedFee, setSelectedFee] = useState(null);

  const feesFetched = useRef(false);
  const roomFetched = useRef(false);

  useEffect(() => {
    if (!feesFetched.current) {
      feesFetched.current = true;
      fetchFees();
    }
    if (!roomFetched.current) {
      roomFetched.current = true;
      fetchRoom();
    }
  }, []);

  const fetchFees = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) throw new Error("Auth token not found!");
      const response = await fetch(`http://localhost:22986/demo/users/unpaid`, {
        method: "GET",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      const data = await response.json();
      if (!Array.isArray(data)) throw new Error("Invalid fee data format");
      setFees(data);
    } catch (err) {
      setError(err.message);
      setFees([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchRoom = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) throw new Error("Auth token not found!");
      const response = await fetch(`http://localhost:22986/demo/users/room`, {
        method: "GET",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      const data = await response.json();
      if (!Array.isArray(data)) throw new Error("Invalid room data format");
      setRoom(data);
    } catch (err) {
      setError(err.message);
      setRoom([]);
    } finally {
      setLoading(false);
    }
  };

  const handleShowModal = (fee) => {
    setSelectedFee(fee);
    setShowModal(true);
  };

  const generateInvoice = async (feeId) => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) throw new Error("Auth token not found!");

      const response = await fetch(`http://localhost:22986/demo/api/bills/generate?feeId=${feeId}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || "Không thể tạo hóa đơn");
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "invoice.docx";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert(`Lỗi: ${err.message}`);
    }
  };

  if (loading) {
    return (
      <div className="resident-loading">
        <div className="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="resident-error">
        <span className="error-icon">⚠️</span>
        <p>Lỗi: {error}</p>
      </div>
    );
  }

  return (
    <div className="resident-page">
      <div className="resident-header">
        <h1>Thông tin cư dân</h1>
        <p>Quản lý phòng và các khoản phí của bạn</p>
      </div>

      <div className="resident-grid">
        {/* QR Code Card */}
        <div className="resident-card qr-card">
          <div className="card-icon">
            <FaQrcode />
          </div>
          <h3>Thanh toán nhanh</h3>
          <p>Quét mã QR để thanh toán các khoản phí</p>
          {!showQR ? (
            <button className="qr-button" onClick={() => setShowQR(true)}>
              Hiển thị mã QR
            </button>
          ) : (
            <div className="qr-image-container">
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Superqr.svg/500px-Superqr.svg.png"
                alt="QR Code"
                className="qr-image"
              />
              <button className="qr-hide-btn" onClick={() => setShowQR(false)}>
                Ẩn mã QR
              </button>
            </div>
          )}
        </div>

        {/* Room Info Card */}
        <div className="resident-card room-card">
          <div className="card-icon room-icon">
            <FaBuilding />
          </div>
          <h3>Thông tin phòng</h3>
          {room.length === 0 ? (
            <p className="no-data">Không có thông tin phòng</p>
          ) : (
            room.map((r) => (
              <div key={r.id} className="room-info">
                <div className="room-info-item">
                  <FaDoorOpen className="info-icon" />
                  <div>
                    <span className="info-label">Số phòng</span>
                    <span className="info-value">{r.roomNumber}</span>
                  </div>
                </div>
                <div className="room-info-item">
                  <FaLayerGroup className="info-icon" />
                  <div>
                    <span className="info-label">Tầng</span>
                    <span className="info-value">{r.floor}</span>
                  </div>
                </div>
                <div className="room-info-item">
                  <FaUsers className="info-icon" />
                  <div>
                    <span className="info-label">Số người</span>
                    <span className="info-value">{r.peopleCount}</span>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Fees Table */}
      <div className="resident-card fees-card">
        <div className="fees-header">
          <div className="fees-title">
            <FaFileInvoiceDollar className="fees-icon" />
            <h3>Danh sách khoản phí</h3>
          </div>
          <span className="fees-count">{fees.length} khoản phí</span>
        </div>

        {fees.length === 0 ? (
          <div className="no-fees">
            <p>🎉 Không có khoản phí nào chưa thanh toán</p>
          </div>
        ) : (
          <div className="fees-table-wrapper">
            <table className="fees-table">
              <thead>
                <tr>
                  <th>Diễn giải</th>
                  <th>Phòng</th>
                  <th>Số tiền</th>
                  <th>Hạn thanh toán</th>
                  <th>Trạng thái</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {fees.map((fee) => (
                  <tr key={fee.id}>
                    <td className="description-cell">{fee.description}</td>
                    <td>{fee.roomNumber}</td>
                    <td className="amount-cell">
                      {fee.amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}
                    </td>
                    <td>{format(new Date(fee.dueDate), "dd/MM/yyyy")}</td>
                    <td>
                      <span className={`status-badge ${fee.status === 'Đã thanh toán' ? 'paid' : 'unpaid'}`}>
                        {fee.status}
                      </span>
                    </td>
                    <td className="actions-cell">
                      <button className="action-btn detail-btn" onClick={() => handleShowModal(fee)}>
                        <FaEye /> Chi tiết
                      </button>
                      <button className="action-btn print-btn" onClick={() => generateInvoice(fee.id)}>
                        <FaPrint /> In
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Fee Detail Modal */}
      <Modal
        title={
          <div className="modal-title-custom">
            <FaFileInvoiceDollar />
            <span>Chi tiết khoản phí</span>
          </div>
        }
        open={showModal}
        onCancel={() => setShowModal(false)}
        footer={[
          <Button key="close" type="primary" onClick={() => setShowModal(false)}>
            Đóng
          </Button>
        ]}
        width={600}
        centered
      >
        {selectedFee && (
          <Descriptions 
            column={1}
            bordered
            size="middle"
            labelStyle={{
              fontWeight: 600,
              backgroundColor: '#f8fafc',
              padding: '12px 16px',
            }}
            contentStyle={{
              padding: '12px 16px',
            }}
          >
            <Descriptions.Item label="Diễn giải">
              {selectedFee.description}
            </Descriptions.Item>
            <Descriptions.Item label="Phòng">
              {selectedFee.roomNumber}
            </Descriptions.Item>
            <Descriptions.Item label="Số tiền">
              <strong style={{ color: '#1a237e', fontSize: '16px' }}>
                {selectedFee.amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}
              </strong>
            </Descriptions.Item>
            <Descriptions.Item label="Hạn thanh toán">
              {format(new Date(selectedFee.dueDate), 'dd/MM/yyyy')}
            </Descriptions.Item>
            <Descriptions.Item label="Trạng thái">
              <Tag 
                color={selectedFee.status === 'Đã thanh toán' ? 'green' : 'red'}
                style={{ margin: 0, padding: '4px 12px' }}
              >
                {selectedFee.status}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default Resident;