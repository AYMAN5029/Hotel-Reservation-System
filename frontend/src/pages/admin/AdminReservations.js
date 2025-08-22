import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Badge, Button, Modal, Form, Spinner, InputGroup } from 'react-bootstrap';
import { reservationAPI, hotelAPI, userAPI } from '../../services/api';
import { toast } from 'react-toastify';
import { format } from 'date-fns';
import { FaSearch, FaEdit, FaTrash, FaEye, FaFilter, FaDownload } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';

const AdminReservations = () => {
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [filteredReservations, setFilteredReservations] = useState([]);
  const [hotels, setHotels] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    filterReservations();
  }, [reservations, searchTerm, statusFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [reservationsResponse, hotelsResponse, usersResponse] = await Promise.all([
        reservationAPI.getAllReservations(),
        hotelAPI.getAllHotels(),
        userAPI.getAllUsers()
      ]);

      setReservations(reservationsResponse.data);
      setHotels(hotelsResponse.data);
      setUsers(usersResponse.data);
    } catch (error) {
      toast.error('Failed to load reservations data');
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterReservations = () => {
    let filtered = reservations;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(reservation => 
        reservation.reservationId.toString().includes(searchTerm) ||
        getHotelName(reservation.hotelId).toLowerCase().includes(searchTerm.toLowerCase()) ||
        getUserName(reservation.userId).toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filter by status
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(reservation => reservation.status === statusFilter);
    }

    // Sort by creation date (newest first)
    filtered.sort((a, b) => new Date(b.createdAt || b.checkInDate) - new Date(a.createdAt || a.checkInDate));

    setFilteredReservations(filtered);
  };

  const getHotelName = (hotelId) => {
    const hotel = hotels.find(h => h.hotelId === hotelId);
    return hotel ? hotel.hotelName : `Hotel #${hotelId}`;
  };

  const getUserName = (userId) => {
    const user = users.find(u => u.userId === userId);
    return user ? user.fullName : `User #${userId}`;
  };

  const calculateRefund = (reservation) => {
    if (reservation.status !== 'CANCELLED') return null;
    
    // Use the cancellation date (updatedAt) instead of today's date
    const cancellationDate = new Date(reservation.updatedAt);
    const checkInDate = new Date(reservation.checkInDate);
    const daysUntilCheckIn = Math.ceil((checkInDate - cancellationDate) / (1000 * 60 * 60 * 24));
    const totalCost = parseFloat(reservation.totalCost);

    let refundPercentage = 0;
    if (daysUntilCheckIn >= 7) {
      refundPercentage = 100;
    } else if (daysUntilCheckIn >= 3) {
      refundPercentage = 75;
    } else if (daysUntilCheckIn >= 1) {
      refundPercentage = 50;
    } else {
      refundPercentage = 0;
    }

    const refundAmount = totalCost * (refundPercentage / 100);
    const deductedAmount = totalCost - refundAmount;

    // Debug logging for the specific reservation
    if (reservation.reservationId === 14) {
      console.log('Reservation 14 calculation:', {
        totalCost,
        daysUntilCheckIn,
        refundPercentage,
        refundAmount,
        deductedAmount
      });
    }

    return {
      refundAmount,
      deductedAmount,
      refundPercentage,
      daysUntilCheckIn
    };
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      'PENDING': { variant: 'warning', text: 'Pending Payment' },
      'CONFIRMED': { variant: 'success', text: 'Confirmed' },
      'CANCELLED': { variant: 'danger', text: 'Cancelled' },
      'COMPLETED': { variant: 'info', text: 'Completed' }
    };

    const config = statusConfig[status] || { variant: 'secondary', text: status };
    return <Badge bg={config.variant}>{config.text}</Badge>;
  };

  const handleViewDetails = (reservation) => {
    setSelectedReservation(reservation);
    setShowDetailsModal(true);
  };

  const handleCancelReservation = async (reservation) => {
    setSelectedReservation(reservation);
    setShowCancelModal(true);
  };

  const confirmCancelReservation = async () => {
    if (!selectedReservation) return;

    try {
      setActionLoading(true);
      await reservationAPI.updateReservation(selectedReservation.reservationId, {
        ...selectedReservation,
        status: 'CANCELLED'
      });
      
      toast.success('Reservation cancelled successfully');
      setShowCancelModal(false);
      setSelectedReservation(null);
      loadData(); // Reload data
    } catch (error) {
      toast.error('Failed to cancel reservation');
      console.error('Error cancelling reservation:', error);
    } finally {
      setActionLoading(false);
    }
  };

  const exportReservations = () => {
    const csvContent = [
      ['Reservation ID', 'Hotel', 'Customer', 'Check-in', 'Check-out', 'Guests', 'Room Type', 'Total Cost', 'Status', 'Created Date'],
      ...filteredReservations.map(reservation => [
        reservation.reservationId,
        getHotelName(reservation.hotelId),
        getUserName(reservation.userId),
        reservation.checkInDate,
        reservation.checkOutDate,
        reservation.numberOfGuests,
        reservation.roomType,
        reservation.totalCost,
        reservation.status,
        reservation.createdAt || reservation.checkInDate
      ])
    ].map(row => row.join(',')).join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reservations_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '400px' }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </Container>
    );
  }

  return (
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col>
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h2>All Reservations</h2>
              <p className="text-muted">Monitor and manage customer reservations</p>
            </div>
            <Button variant="outline-primary" onClick={exportReservations}>
              <FaDownload className="me-2" />
              Export CSV
            </Button>
          </div>
        </Col>
      </Row>

      {/* Revenue Summary */}
      <Row className="mb-4">
        <Col>
          <Card className="bg-light">
            <Card.Body>
              <Row>
                <Col md={3}>
                  <div className="text-center">
                    <h4 className="text-success mb-1">
                      ₹{(() => {
                        const confirmedRevenue = reservations
                          .filter(r => r.status === 'CONFIRMED' || r.status === 'COMPLETED')
                          .reduce((sum, r) => sum + parseFloat(r.totalCost), 0);
                        const cancellationRevenue = reservations
                          .filter(r => r.status === 'CANCELLED')
                          .reduce((sum, r) => {
                            const refunded = r.refundedAmount !== null && r.refundedAmount !== undefined ? r.refundedAmount : 0;
                            const deductedAmount = r.totalCost - refunded;
                            console.log(`Reservation ${r.reservationId}: total=${r.totalCost}, refunded=${refunded}, deducted=${deductedAmount}`);
                            return sum + deductedAmount;
                          }, 0);
                        console.log('Total cancellation revenue:', cancellationRevenue);
                        console.log('Confirmed revenue:', confirmedRevenue);
                        return (confirmedRevenue + cancellationRevenue).toFixed(2);
                      })()}
                    </h4>
                    <small className="text-muted">Total Revenue</small>
                  </div>
                </Col>
                <Col md={3}>
                  <div className="text-center">
                    <h4 className="text-info mb-1">
                      ₹{(() => {
                        return reservations
                          .filter(r => r.status === 'CANCELLED')
                          .reduce((sum, r) => {
                            const refunded = r.refundedAmount !== null && r.refundedAmount !== undefined ? r.refundedAmount : 0;
                            const deductedAmount = r.totalCost - refunded;
                            return sum + deductedAmount;
                          }, 0)
                          .toFixed(2);
                      })()}
                    </h4>
                    <small className="text-muted">Revenue from Cancellations</small>
                  </div>
                </Col>
                <Col md={3}>
                  <div className="text-center">
                    <h4 className="text-warning mb-1">
                      ₹{(() => {
                        return reservations
                          .filter(r => r.status === 'CANCELLED')
                          .reduce((sum, r) => {
                            const refunded = r.refundedAmount !== null && r.refundedAmount !== undefined ? r.refundedAmount : 0;
                            return sum + refunded;
                          }, 0)
                          .toFixed(2);
                      })()}
                    </h4>
                    <small className="text-muted">Total Refunded</small>
                  </div>
                </Col>
                <Col md={3}>
                  <div className="text-center">
                    <h4 className="text-primary mb-1">
                      ₹{(() => {
                        const confirmedRevenue = reservations
                          .filter(r => r.status === 'CONFIRMED' || r.status === 'COMPLETED')
                          .reduce((sum, r) => sum + parseFloat(r.totalCost), 0);
                        const cancellationRevenue = reservations
                          .filter(r => r.status === 'CANCELLED')
                          .reduce((sum, r) => {
                            const refund = calculateRefund(r);
                            return sum + (refund ? refund.deductedAmount : 0);
                          }, 0);
                        return (confirmedRevenue + cancellationRevenue).toFixed(2);
                      })()}
                    </h4>
                    <small className="text-muted">Net Revenue</small>
                  </div>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Filters */}
      <Row className="mb-4">
        <Col md={6}>
          <InputGroup>
            <InputGroup.Text>
              <FaSearch />
            </InputGroup.Text>
            <Form.Control
              type="text"
              placeholder="Search by reservation ID, hotel name, or customer name..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </InputGroup>
        </Col>
        <Col md={3}>
          <Form.Select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All Statuses</option>
            <option value="PENDING">Pending Payment</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="CANCELLED">Cancelled</option>
            <option value="COMPLETED">Completed</option>
          </Form.Select>
        </Col>
        <Col md={3}>
          <div className="text-muted">
            Showing {filteredReservations.length} of {reservations.length} reservations
          </div>
        </Col>
      </Row>

      {/* Reservations Table */}
      <Row>
        <Col>
          <Card>
            <Card.Body>
              {filteredReservations.length === 0 ? (
                <div className="text-center py-5">
                  <p className="text-muted">No reservations found matching your criteria</p>
                </div>
              ) : (
                <Table responsive hover>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Hotel</th>
                      <th>Customer</th>
                      <th>Check-in</th>
                      <th>Check-out</th>
                      <th>Guests</th>
                      <th>Room Type</th>
                      <th>Total Cost</th>
                      <th>Revenue</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredReservations.map((reservation) => (
                      <tr key={reservation.reservationId}>
                        <td>#{reservation.reservationId}</td>
                        <td>{getHotelName(reservation.hotelId)}</td>
                        <td>{getUserName(reservation.userId)}</td>
                        <td>{format(new Date(reservation.checkInDate), 'MMM dd, yyyy')}</td>
                        <td>{format(new Date(reservation.checkOutDate), 'MMM dd, yyyy')}</td>
                        <td>{reservation.numberOfGuests}</td>
                        <td>{reservation.roomType}</td>
                        <td>₹{reservation.totalCost}</td>
                        <td>
                          {(() => {
                            const refund = calculateRefund(reservation);
                            if (refund) {
                              return (
                                <div>
                                  <span className="text-success">₹{refund.deductedAmount.toFixed(2)}</span>
                                  <br />
                                  <small className="text-muted">
                                    (₹{refund.refundAmount.toFixed(2)} refunded)
                                  </small>
                                </div>
                              );
                            } else if (reservation.status === 'CONFIRMED' || reservation.status === 'COMPLETED') {
                              return <span className="text-success">₹{reservation.totalCost}</span>;
                            } else {
                              return <span className="text-muted">₹0</span>;
                            }
                          })()}
                        </td>
                        <td>{getStatusBadge(reservation.status)}</td>
                        <td>
                          <div className="d-flex gap-1">
                            <Button
                              variant="outline-info"
                              size="sm"
                              onClick={() => handleViewDetails(reservation)}
                              title="View Details"
                            >
                              <FaEye />
                            </Button>
                            {reservation.status !== 'CANCELLED' && reservation.status !== 'COMPLETED' && (
                              <Button
                                variant="outline-danger"
                                size="sm"
                                onClick={() => handleCancelReservation(reservation)}
                                title="Cancel Reservation"
                              >
                                <FaTrash />
                              </Button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Reservation Details Modal */}
      <Modal show={showDetailsModal} onHide={() => setShowDetailsModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Reservation Details</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedReservation && (
            <Row>
              <Col md={6}>
                <h6>Reservation Information</h6>
                <p><strong>ID:</strong> #{selectedReservation.reservationId}</p>
                <p><strong>Status:</strong> {getStatusBadge(selectedReservation.status)}</p>
                <p><strong>Check-in:</strong> {format(new Date(selectedReservation.checkInDate), 'MMMM dd, yyyy')}</p>
                <p><strong>Check-out:</strong> {format(new Date(selectedReservation.checkOutDate), 'MMMM dd, yyyy')}</p>
                <p><strong>Guests:</strong> {selectedReservation.numberOfGuests}</p>
                <p><strong>Room Type:</strong> {selectedReservation.roomType}</p>
                <p><strong>Total Cost:</strong> ${selectedReservation.totalCost}</p>
              </Col>
              <Col md={6}>
                <h6>Hotel Information</h6>
                <p><strong>Hotel:</strong> {getHotelName(selectedReservation.hotelId)}</p>
                
                <h6>Customer Information</h6>
                <p><strong>Customer:</strong> {getUserName(selectedReservation.userId)}</p>
                
                {selectedReservation.specialRequests && (
                  <>
                    <h6>Special Requests</h6>
                    <p>{selectedReservation.specialRequests}</p>
                  </>
                )}
              </Col>
            </Row>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDetailsModal(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Cancel Confirmation Modal */}
      <Modal show={showCancelModal} onHide={() => setShowCancelModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Cancel Reservation</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Are you sure you want to cancel this reservation?</p>
          {selectedReservation && (
            <div className="bg-light p-3 rounded">
              <p><strong>Reservation ID:</strong> #{selectedReservation.reservationId}</p>
              <p><strong>Hotel:</strong> {getHotelName(selectedReservation.hotelId)}</p>
              <p><strong>Customer:</strong> {getUserName(selectedReservation.userId)}</p>
              <p><strong>Total Cost:</strong> ${selectedReservation.totalCost}</p>
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCancelModal(false)}>
            No, Keep Reservation
          </Button>
          <Button 
            variant="danger" 
            onClick={confirmCancelReservation}
            disabled={actionLoading}
          >
            {actionLoading ? (
              <>
                <Spinner size="sm" className="me-2" />
                Cancelling...
              </>
            ) : (
              'Yes, Cancel Reservation'
            )}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default AdminReservations;
