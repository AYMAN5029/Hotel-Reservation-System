import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Badge, Modal, Form, Spinner } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContext';
import { reservationAPI, hotelAPI } from '../services/api';
import { toast } from 'react-toastify';
import { format } from 'date-fns';
import { FaCalendarAlt, FaUsers, FaEdit, FaTrash, FaCreditCard } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';

const MyReservations = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [editData, setEditData] = useState({});
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    loadReservations();
  }, []);

  const loadReservations = async () => {
    try {
      // In a real app, this would be getUserReservations(user.userId)
      const response = await reservationAPI.getAllReservations();
      // Filter by current user (this should be done on backend)
      const userReservations = response.data.filter(res => res.userId === user.userId);
      
      // Fetch hotel names for each reservation
      const reservationsWithHotelNames = await Promise.all(
        userReservations.map(async (reservation) => {
          try {
            const hotelResponse = await hotelAPI.getHotelById(reservation.hotelId);
            return {
              ...reservation,
              hotelName: hotelResponse.data.hotelName
            };
          } catch (error) {
            console.error(`Failed to fetch hotel ${reservation.hotelId}:`, error);
            return {
              ...reservation,
              hotelName: `Hotel #${reservation.hotelId}`
            };
          }
        })
      );
      
      setReservations(reservationsWithHotelNames);
    } catch (error) {
      toast.error('Failed to load reservations');
      console.error('Error loading reservations:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleEditReservation = (reservation) => {
    setSelectedReservation(reservation);
    setEditData({
      checkInDate: reservation.checkInDate,
      checkOutDate: reservation.checkOutDate,
      numberOfGuests: reservation.numberOfGuests,
      roomType: reservation.roomType,
      specialRequests: reservation.specialRequests || ''
    });
    setShowEditModal(true);
  };

  const handleCancelReservation = (reservation) => {
    setSelectedReservation(reservation);
    setShowCancelModal(true);
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setActionLoading(true);

    try {
      await reservationAPI.updateReservation(selectedReservation.reservationId, {
        ...selectedReservation,
        ...editData
      });
      toast.success('Reservation updated successfully!');
      setShowEditModal(false);
      loadReservations();
    } catch (error) {
      toast.error('Failed to update reservation');
      console.error('Update error:', error);
    } finally {
      setActionLoading(false);
    }
  };

  const handleCancelConfirm = async () => {
    setActionLoading(true);

    try {
      // Use the backend cancellation endpoint that calculates refund
      await reservationAPI.cancelReservation(selectedReservation.reservationId);
      toast.success('Reservation cancelled successfully!');
      setShowCancelModal(false);
      loadReservations();
    } catch (error) {
      toast.error('Failed to cancel reservation');
      console.error('Cancel error:', error);
    } finally {
      setActionLoading(false);
    }
  };

  const handleEditChange = (e) => {
    setEditData({
      ...editData,
      [e.target.name]: e.target.value
    });
  };

  const getStatusBadge = (status) => {
    const variants = {
      'CONFIRMED': 'success',
      'PENDING': 'warning',
      'CANCELLED': 'danger',
      'REFUNDED': 'info',
      'COMPLETED': 'info'
    };
    return <Badge bg={variants[status] || 'secondary'}>{status}</Badge>;
  };

  const getTodayDate = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };

  const calculateRefund = (reservation) => {
    const today = new Date();
    const checkInDate = new Date(reservation.checkInDate);
    const daysUntilCheckIn = Math.ceil((checkInDate - today) / (1000 * 60 * 60 * 24));
    const totalCost = parseFloat(reservation.totalCost);

    if (daysUntilCheckIn >= 7) {
      return { percentage: 100, amount: totalCost };
    } else if (daysUntilCheckIn >= 3) {
      return { percentage: 75, amount: totalCost * 0.75 };
    } else if (daysUntilCheckIn >= 1) {
      return { percentage: 50, amount: totalCost * 0.50 };
    } else {
      return { percentage: 0, amount: 0 };
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <Row>
        <Col>
          <h2 className="mb-4">My Reservations</h2>
        </Col>
      </Row>

      {reservations.length === 0 ? (
        <Row>
          <Col className="text-center py-5">
            <h4>No reservations found</h4>
            <p className="text-muted">You haven't made any reservations yet.</p>
            <Button variant="primary" onClick={() => navigate('/search')}>
              Search Hotels
            </Button>
          </Col>
        </Row>
      ) : (
        <Row>
          {reservations.map((reservation) => (
            <Col md={6} lg={4} key={reservation.reservationId} className="mb-4">
              <Card className="reservation-card h-100">
                <Card.Body>
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <h5 className="card-title">{reservation.hotelName || `Hotel #${reservation.hotelId}`}</h5>
                    {getStatusBadge(reservation.status)}
                  </div>

                  <div className="mb-2">
                    <FaCalendarAlt className="me-2 text-muted" />
                    <small>
                      {format(new Date(reservation.checkInDate), 'MMM dd, yyyy')} - {' '}
                      {format(new Date(reservation.checkOutDate), 'MMM dd, yyyy')}
                    </small>
                  </div>

                  <div className="mb-2">
                    <FaUsers className="me-2 text-muted" />
                    <small>{reservation.numberOfGuests} Guest(s)</small>
                  </div>

                  <div className="mb-2">
                    <small><strong>Room Type:</strong> {reservation.roomType}</small>
                  </div>

                  <div className="mb-3">
                    <small><strong>Total Cost:</strong> ₹{reservation.totalCost}</small>
                    {reservation.status === 'CANCELLED' && reservation.refundedAmount !== undefined && (
                      <div className="mt-2">
                        <small className="text-success">
                          <strong>Refunded:</strong> ₹{reservation.refundedAmount.toFixed(2)}
                        </small>
                        <br />
                        <small className="text-muted">
                          (Refund processed based on cancellation policy)
                        </small>
                      </div>
                    )}
                  </div>

                  {reservation.specialRequests && (
                    <div className="mb-3">
                      <small className="text-muted">
                        <strong>Special Requests:</strong> {reservation.specialRequests}
                      </small>
                    </div>
                  )}

                  <div className="d-flex gap-2">
                    {reservation.status === 'PENDING' && (
                      <Button
                        variant="success"
                        size="sm"
                        onClick={() => navigate(`/payment/${reservation.reservationId}`)}
                      >
                        <FaCreditCard className="me-1" />
                        Pay Now
                      </Button>
                    )}
                    {reservation.status === 'CONFIRMED' && (
                      <Button
                        variant="outline-primary"
                        size="sm"
                        onClick={() => handleEditReservation(reservation)}
                      >
                        <FaEdit className="me-1" />
                        Edit
                      </Button>
                    )}
                    {(reservation.status === 'PENDING' || reservation.status === 'CONFIRMED') && (
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleCancelReservation(reservation)}
                      >
                        <FaTrash className="me-1" />
                        Cancel
                      </Button>
                    )}
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      {/* Edit Reservation Modal */}
      <Modal show={showEditModal} onHide={() => setShowEditModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Edit Reservation</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleEditSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Check-in Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="checkInDate"
                    value={editData.checkInDate}
                    onChange={handleEditChange}
                    min={getTodayDate()}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Check-out Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="checkOutDate"
                    value={editData.checkOutDate}
                    onChange={handleEditChange}
                    min={editData.checkInDate || getTodayDate()}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Number of Guests</Form.Label>
                  <Form.Control
                    type="number"
                    name="numberOfGuests"
                    value={editData.numberOfGuests}
                    onChange={handleEditChange}
                    min="1"
                    max="10"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Room Type</Form.Label>
                  <Form.Select
                    name="roomType"
                    value={editData.roomType}
                    onChange={handleEditChange}
                    required
                  >
                    <option value="AC">AC Room</option>
                    <option value="NON_AC">Non-AC Room</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Special Requests</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="specialRequests"
                value={editData.specialRequests}
                onChange={handleEditChange}
                placeholder="Any special requests or preferences..."
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowEditModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={actionLoading}>
              {actionLoading ? 'Updating...' : 'Update Reservation'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Cancel Confirmation Modal */}
      <Modal show={showCancelModal} onHide={() => setShowCancelModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Cancel Reservation</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Are you sure you want to cancel this reservation?</p>
          {selectedReservation && (
            <>
              <div className="bg-light p-3 rounded mb-3">
                <strong>Reservation Details:</strong>
                <br />
                Hotel: {selectedReservation.hotelName || `Hotel #${selectedReservation.hotelId}`}
                <br />
                Dates: {format(new Date(selectedReservation.checkInDate), 'MMM dd, yyyy')} - {' '}
                {format(new Date(selectedReservation.checkOutDate), 'MMM dd, yyyy')}
                <br />
                Total Cost: ₹{selectedReservation.totalCost}
              </div>
              
              {(() => {
                const refund = calculateRefund(selectedReservation);
                return (
                  <div className="bg-info bg-opacity-10 p-3 rounded mb-3">
                    <strong>Refund Information:</strong>
                    <br />
                    <span className="text-success">
                      You will receive ₹{refund.amount.toFixed(2)} ({refund.percentage}% refund)
                    </span>
                    <br />
                    <small className="text-muted">
                      {refund.percentage === 100 && "Full refund - Cancelled 7+ days before check-in"}
                      {refund.percentage === 75 && "75% refund - Cancelled 3-6 days before check-in"}
                      {refund.percentage === 50 && "50% refund - Cancelled 1-2 days before check-in"}
                      {refund.percentage === 0 && "No refund - Cancelled on check-in day"}
                    </small>
                  </div>
                );
              })()}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCancelModal(false)}>
            Keep Reservation
          </Button>
          <Button variant="danger" onClick={handleCancelConfirm} disabled={actionLoading}>
            {actionLoading ? 'Cancelling...' : 'Cancel Reservation'}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default MyReservations;
